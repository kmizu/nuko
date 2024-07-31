package com.github.nuko

import scala.jdk.CollectionConverters._
import com.github.nuko._
import com.github.nuko.Type._
import com.github.nuko.TypedAst.{FunctionLiteral, TypedNode, ValueNode}
import com.github.nuko.Value.*

import java.awt.Graphics
import java.net.http.HttpClient
import java.net.{URI, URL}
import java.nio.file.{Files, Path}
import javax.swing.WindowConstants
import scala.runtime.BoxedUnit

/**
 * @author Kota Mizushima
 */
class NukoInterpreter extends Processor[TypedAst.Program, Value, InteractiveSession] {interpreter =>
  def reportError(message: String): Nothing = {
    throw InterpreterException(None, message)
  }

  def findMethod(self: AnyRef, name: String, params: Array[Value]): MethodSearchResult = {
    val selfClass = self.getClass
    val nameMatchedMethods = selfClass.getMethods.filter {
      _.getName == name
    }
    val maybeUnboxedMethod = nameMatchedMethods.find { m =>
      val parameterCountMatches = m.getParameterCount == params.length
      val parameterTypes = Value.classesOfValues(params)
      val parameterTypesMatches = (m.getParameterTypes zip parameterTypes).forall{ case (arg, param) =>
        arg.isAssignableFrom(param)
      }
      parameterCountMatches && parameterTypesMatches
    }.map{m =>
      m.setAccessible(true)
      UnboxedVersionMethodFound(m)
    }
    val maybeBoxedMethod = {
      nameMatchedMethods.find{m =>
        val parameterCountMatches = m.getParameterCount == params.length
        val boxedParameterTypes = Value.boxedClassesOfValues(params)
        val boxedParameterTypesMatches = (m.getParameterTypes zip boxedParameterTypes).forall{ case (arg, param) =>
          arg.isAssignableFrom(param)
        }
        parameterCountMatches && boxedParameterTypesMatches
      }
    }.map{m =>
      m.setAccessible(true)
      BoxedVersionMethodFound(m)
    }
    maybeUnboxedMethod.orElse(maybeBoxedMethod).getOrElse(NoMethodFound)
  }

  def findConstructor(target: Class[_], params: Array[Value]): ConstructorSearchResult = {
    val constructors = target.getConstructors
    val maybeUnboxedConstructor = constructors.find{c =>
      val parameterCountMatches = c.getParameterCount == params.length
      val unboxedParameterTypes = Value.classesOfValues(params)
      val parameterTypesMatches  = (c.getParameterTypes zip unboxedParameterTypes).forall{ case (arg, param) =>
        arg.isAssignableFrom(param)
      }
      parameterCountMatches && parameterTypesMatches
    }.map{c =>
      UnboxedVersionConstructorFound(c)
    }
    val maybeBoxedConstructor = {
      constructors.find{c =>
        val parameterCountMatches = c.getParameterCount == params.length
        val boxedParameterTypes = Value.boxedClassesOfValues(params)
        val parameterTypesMatches  = (c.getParameterTypes zip boxedParameterTypes).forall{ case (arg, param) =>
          arg.isAssignableFrom(param)
        }
        parameterCountMatches && parameterTypesMatches
      }
    }.map { c =>
      BoxedVersionConstructorFound(c)
    }
    maybeUnboxedConstructor.orElse(maybeBoxedConstructor).getOrElse(NoConstructorFound)
  }

  object BuiltinEnvironment extends RuntimeEnvironment(None) {
    define("部分文字列"){ case List(ObjectValue(s:String), begin: BoxedInt, end: BoxedInt) =>
      ObjectValue(s.substring(begin.value.toInt, end.value.toInt))
    }

    define("文字を取得") { case List(ObjectValue(s:String), index: BoxedInt) =>
      ObjectValue(s.substring(index.value.toInt, index.value.toInt + 1))
    }

    define("マッチする") { case List(ObjectValue(s: String), ObjectValue(regex: String)) =>
      BoxedBoolean(s.matches(regex))
    }

    define("平方根") { case List(BoxedReal(value)) =>
      BoxedReal(math.sqrt(value.toDouble))
    }

    define("整数") { case List(BoxedReal(value)) =>
      BoxedInt(value.toInt)
    }

    define("小数") { case List(BoxedInt(value)) =>
      BoxedReal(value.toDouble)
    }

    define("切り捨て") { case List(BoxedReal(value)) =>
      BoxedInt(value.toInt)
    }

    define("切り上げ") { case List(BoxedReal(value)) =>
      BoxedInt(math.ceil(value.toDouble).toInt)
    }

    define("絶対値") { case List(BoxedReal(value)) =>
      BoxedReal(math.abs(value.toDouble))
    }

    define("スレッド開始") { case List(fun: FunctionValue) =>
      new Thread((() => {
          val env = new RuntimeEnvironment(fun.environment)
          interpreter.evaluate(TypedAst.FunctionCall(TDynamic, NoLocation, fun.value, Nil), env)
      }):Runnable).start()
      UnitValue
    }

    define("表示") { case List(param) =>
      println(param)
      param
    }

    define("エラー表示") { case List(param) =>
      Console.err.println(param)
      param
    }

    define("時間を計測する") { case List(fun: FunctionValue) =>
      val env = new RuntimeEnvironment(fun.environment)
      val start = System.currentTimeMillis()
      interpreter.evaluate(TypedAst.FunctionCall(TDynamic, NoLocation, fun.value, List()), env)
      val end = System.currentTimeMillis()
      BoxedInt((end - start).toInt)
    }
    define("休眠する"){ case List(milliseconds: BoxedInt) =>
      Thread.sleep(milliseconds.value.toLong)
      UnitValue
    }

    define("変換") { case List(ObjectValue(list: java.util.List[_])) =>
      NativeFunctionValue{
        case List(fun: FunctionValue) =>
          val newList = new java.util.ArrayList[Any]
          val env = new RuntimeEnvironment(fun.environment)
          var i = 0
          while(i < list.size()) {
            val param: Value = Value.toKlassic(list.get(i).asInstanceOf[AnyRef])
            val result: Value = performFunctionInternal(fun.value, List(ValueNode(param)), env)
            newList.add(Value.fromKlassic(result))
            i += 1
          }
          ObjectValue(newList)
      }
    }

    define("確認") { case List(BoxedBoolean(condition)) =>
        if(!condition) sys.error("assertion failure") else UnitValue
    }

    define("一致を確認") { case List(a: Value) =>
      NativeFunctionValue{
        case List(b: Value) =>
          if(a != b) sys.error(s"expected: ${a}, actual: ${b}") else UnitValue
      }
    }

    define("先頭") { case List(ObjectValue(list: java.util.List[_])) =>
      Value.toKlassic(list.get(0).asInstanceOf[AnyRef])
    }
    define("末尾") { case List(ObjectValue(list: java.util.List[_])) =>
      Value.toKlassic(list.subList(1, list.size()))
    }
    define("構築") { case List(value: Value) =>
      NativeFunctionValue{ case List(ObjectValue(list: java.util.List[_])) =>
        val newList = new java.util.ArrayList[Any]
        var i = 0
        newList.add(Value.fromKlassic(value))
        while(i < list.size()) {
          newList.add(list.get(i))
          i += 1
        }
        Value.toKlassic(newList)
      }
    }
    define("サイズ") { case List(ObjectValue(list: java.util.List[_])) =>
      BoxedInt(list.size())
    }
    define("空である") { case List(ObjectValue(list: java.util.List[_])) =>
      BoxedBoolean(list.isEmpty)
    }
    define("後で埋める") { case Nil =>
      sys.error("まだ実装されていません")
    }
    define("URI") { case List(ObjectValue(value: String)) =>
      ObjectValue(new URI(value))
    }
    define("URL") { case List(ObjectValue(value: String)) =>
      ObjectValue(new URI(value).toURL)
    }
    define("たたむ") { case List(ObjectValue(list: java.util.List[_])) =>
      NativeFunctionValue{ case List(init: Value) =>
        NativeFunctionValue { case List(fun: FunctionValue) =>
          val env = new RuntimeEnvironment(fun.environment)
          var i = 0
          var result: Value = init
          while(i < list.size()) {
            val params: List[TypedNode] = List(ValueNode(result), ValueNode(Value.toKlassic(list.get(i).asInstanceOf[AnyRef])))
            result = performFunctionInternal(fun.value, params, env)
            i += 1
          }
          result
        }
      }
    }

    object mainWindow extends javax.swing.JFrame("メインウィンドウ") {
      val operations = scala.collection.mutable.Buffer.empty[Graphics => Unit]
      setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
      setSize(500, 500)
      val content = new javax.swing.JPanel {
        override def paintComponent(g: Graphics): Unit = {
          super.paintComponent(g)
          g.setColor(java.awt.Color.BLACK)
          for(operation <- operations){
            operation(g)
          }
        }
      }
      add(content)
    }

    define("線をひく") { case List(BoxedInt(x1), BoxedInt(y1), BoxedInt(x2), BoxedInt(y2)) =>
      mainWindow.operations.append({g =>
        g.drawLine(x1.toInt, y1.toInt, x2.toInt, y2.toInt)
      })
      mainWindow.setVisible(true)
      UnitValue
    }
    define("desktop") { case Nil =>
      ObjectValue(java.awt.Desktop.getDesktop())
    }
    defineValue("無")(
      ObjectValue(null)
    )
  }

  object BuiltinRecordEnvironment extends RecordEnvironment() {
    define("点")(
      "x" -> TInt,
      "y" -> TInt
    )
  }

  object BuiltinModuleEnvironment extends ModuleEnvironment() {
    private final val LIST= "リスト"
    private final val DICTIONARY = "辞書"
    private final val SET = "集合"
    private final val FILE = "ファイル"
    private final val WEB = "ウェブ"
    private final val INT = "整数"
    private final val STRING = "文字列"
    enter(INT) {
      define("構築") { case List(value: Value) =>
        NativeFunctionValue { case List(ObjectValue(list: java.util.List[_])) =>
          val newList = new java.util.ArrayList[Any]
          var i = 0
          newList.add(Value.fromKlassic(value))
          while (i < list.size()) {
            newList.add(list.get(i))
            i += 1
          }
          Value.toKlassic(newList)
        }
      }
    }
    enter(STRING) {
      define("マッチする") { case List(ObjectValue(s: String)) =>
        NativeFunctionValue { case List(ObjectValue(regex: String)) =>
          BoxedBoolean(s.matches(regex))
        }
      }
    }
    enter(LIST) {
      define("先頭") { case List(ObjectValue(list: java.util.List[_])) =>
        println("list: " + list)
        Value.toKlassic(list.get(0).asInstanceOf[AnyRef])
      }
      define("末尾") { case List(ObjectValue(list: java.util.List[_])) =>
        Value.toKlassic(list.subList(1, list.size()))
      }
      define("構築") { case List(value: Value) =>
        NativeFunctionValue { case List(ObjectValue(list: java.util.List[_])) =>
          val newList = new java.util.ArrayList[Any]
          var i = 0
          newList.add(Value.fromKlassic(value))
          while (i < list.size()) {
            newList.add(list.get(i))
            i += 1
          }
          Value.toKlassic(newList)
        }
      }
      define("削除") { case List(ObjectValue(self: java.util.List[_])) =>
        NativeFunctionValue{ case List(a: Value) =>
          val newList = new java.util.ArrayList[Any]
          for(v <- self.asScala) {
            newList.add(v)
          }
          newList.remove(Value.fromKlassic(a))
          ObjectValue(newList)
        }
      }
      define("サイズ") { case List(ObjectValue(list: java.util.List[_])) =>
        BoxedInt(list.size())
      }
      define("空である") { case List(ObjectValue(list: java.util.List[_])) =>
        BoxedBoolean(list.isEmpty)
      }
      define("変換") { case List(ObjectValue(list: java.util.List[_])) =>
        NativeFunctionValue{
          case List(fun: FunctionValue) =>
            val newList = new java.util.ArrayList[Any]
            val env = new RuntimeEnvironment(fun.environment)
            var i = 0
            while(i < list.size()) {
              val param: Value = Value.toKlassic(list.get(i).asInstanceOf[AnyRef])
              val result: Value = performFunctionInternal(fun.value, List(ValueNode(param)), env)
              newList.add(Value.fromKlassic(result))
              i += 1
            }
            ObjectValue(newList)
        }
      }
      define("たたむ") { case List(ObjectValue(list: java.util.List[_])) =>
        NativeFunctionValue{ case List(init: Value) =>
          NativeFunctionValue { case List(fun: FunctionValue) =>
            val env = new RuntimeEnvironment(fun.environment)
            var i = 0
            var result: Value = init
            while(i < list.size()) {
              val params: List[TypedNode] = List(ValueNode(result), ValueNode(Value.toKlassic(list.get(i).asInstanceOf[AnyRef])))
              result = performFunctionInternal(fun.value, params, env)
              i += 1
            }
            result
          }
        }
      }
    }
    enter(FILE) {
      define("読み込む"){ case List(ObjectValue(path: String)) =>
        ObjectValue(Files.readString(Path.of(path)))
      }
      define("書き込む"){ case List(ObjectValue(path: String)) =>
        NativeFunctionValue{ case List(ObjectValue(content: String)) =>
          Files.writeString(Path.of(path), content)
          UnitValue
        }
      }
    }
    enter(WEB) {
      define("読み込む") { case List(ObjectValue(url: String)) =>
        ObjectValue(scala.io.Source.fromURL(url).mkString)
      }
    }
    enter(DICTIONARY) {
      define("追加") { case List(ObjectValue(self: java.util.Map[_, _])) =>
        NativeFunctionValue{ case List(a: Value, b: Value) =>
          val newMap = new java.util.HashMap[Any, Any]()
          for((k, v) <- self.asScala) {
            newMap.put(k, v)
          }
          newMap.put(Value.fromKlassic(a), Value.fromKlassic(b))
          ObjectValue(newMap)
        }
      }
      define("キーを含む") { case List(ObjectValue(self: java.util.Map[_, _])) =>
        NativeFunctionValue{ case List(k: Value) =>
          BoxedBoolean(self.containsKey(Value.fromKlassic(k)))
        }
      }
      define("値を含む") { case List(ObjectValue(self: java.util.Map[_, _])) =>
        NativeFunctionValue{ case List(v: Value) =>
          BoxedBoolean(self.containsValue(Value.fromKlassic(v)))
        }
      }
      define("値を取得") { case List(ObjectValue(self: java.util.Map[_, _])) =>
        NativeFunctionValue{ case List(k: Value) =>
          Value.toKlassic(self.get(Value.fromKlassic(k)).asInstanceOf[AnyRef])
        }
      }
      define("サイズ") { case List(ObjectValue(self: java.util.Map[_, _])) =>
        BoxedInt(self.size())
      }
      define("空である") { case List(ObjectValue(map: java.util.Map[_, _])) =>
        BoxedBoolean(map.isEmpty)
      }
    }
    enter(SET) {
      define("追加") { case List(ObjectValue(self: java.util.Set[_])) =>
        NativeFunctionValue{ case List(a: Value) =>
          val newSet = new java.util.HashSet[Any]()
          for(v <- self.asScala) {
            newSet.add(v)
          }
          newSet.add(Value.fromKlassic(a))
          ObjectValue(newSet)
        }
      }
      define("削除") { case List(ObjectValue(self: java.util.Set[_])) =>
        NativeFunctionValue{ case List(a: Value) =>
          val newSet = new java.util.HashSet[Any]()
          for(v <- self.asScala) {
            newSet.add(v)
          }
          newSet.remove(Value.fromKlassic(a))
          ObjectValue(newSet)
        }
      }
      define("要素を含む") { case List(ObjectValue(self: java.util.Set[_])) =>
        NativeFunctionValue { case List(a: Value) =>
          BoxedBoolean(self.contains(Value.fromKlassic(a)))
        }
      }
      define("サイズ") { case List(ObjectValue(self: java.util.Set[_])) =>
        BoxedInt(self.size())
      }
      define("空である") { case List(ObjectValue(self: java.util.Set[_])) =>
        BoxedBoolean(self.isEmpty)
      }
    }
  }

  def toList(row: Type): List[(String, Type)] = row match {
    case tv@TVariable(_) => sys.error("cannot reach here")
    case TRowExtend(l, t, extension) => (l -> t) :: toList(extension)
    case TRowEmpty => Nil
    case otherwise => throw TyperPanic(None, "Unexpected: " + otherwise)
  }

  final def interpret(program: TypedAst.Program, session: InteractiveSession): Value = {
    val runtimeRecordEnvironment: RecordEnvironment = BuiltinRecordEnvironment
    program.records.foreach { case (name, record) =>
      val members = toList(record.row)
      val rmembers = members.map { case (n, t) => n -> t }
      runtimeRecordEnvironment.records += (name -> rmembers)
    }
    interpreter.evaluate(program.block, env = BuiltinEnvironment, recordEnv = runtimeRecordEnvironment, moduleEnv = BuiltinModuleEnvironment)
  }

  private def evaluate(node: TypedNode): Value = {
    evaluate(node, BuiltinEnvironment)
  }

  private def performFunctionInternal(func: TypedNode, params: List[TypedNode], env: RuntimeEnvironment): Value = {
    performFunction(TypedAst.FunctionCall(TDynamic, NoLocation, func, params), env)
  }

  private def performFunction(node: TypedAst.FunctionCall, env: RuntimeEnvironment): Value = node match {
    case TypedAst.FunctionCall(type_, location, function, params) =>
      evaluate(function, env) match {
        case FunctionValue(TypedAst.FunctionLiteral(type_, location, fparams, optionalType, proc), cenv) =>
          val local = new RuntimeEnvironment(cenv)
          (fparams zip params).foreach{ case (fp, ap) =>
            local(fp.name) = evaluate(ap, env)
          }
          evaluate(proc, local)
        case NativeFunctionValue(body) =>
          val actualParams = params.map{p => evaluate(p, env)}
          if(body.isDefinedAt(actualParams)) {
            body(params.map{p => evaluate(p, env)})
          } else {
            reportError("parameters are not matched to the function's arguments: " + actualParams)
          }
        case _ =>
          reportError("unknown error")
      }
  }

  private def evaluate(node: TypedNode, env: RuntimeEnvironment, recordEnv: RecordEnvironment=BuiltinRecordEnvironment, moduleEnv: ModuleEnvironment = BuiltinModuleEnvironment): Value = {
    def evalRecursive(node: TypedNode): Value = {
      node match{
        case TypedAst.Block(type_, location, expressions) =>
          val local = new RuntimeEnvironment(Some(env))
          expressions.foldLeft(UnitValue:Value){(result, x) => evaluate(x, local)}
        case TypedAst.WhileExpression(type_, location, cond, body) =>
          while(evalRecursive(cond) == BoxedBoolean(true)) {
            evalRecursive(body)
          }
          UnitValue
        case TypedAst.IfExpression(type_, location, condition, pos, neg) =>
          evalRecursive(condition) match {
            case BoxedBoolean(true) => evalRecursive(pos)
            case BoxedBoolean(false) => evalRecursive(neg)
            case _ => reportError("type error")
          }
        case TypedAst.BinaryExpression(type_, location, Operator.AND2, lhs, rhs) =>
          evalRecursive(lhs) match {
            case BoxedBoolean(true) => evalRecursive(rhs)
            case BoxedBoolean(false) => BoxedBoolean(false)
            case _ => reportError("type error")
          }
        case TypedAst.BinaryExpression(type_, location, Operator.BAR2, lhs, rhs) =>
          evalRecursive(lhs) match {
            case BoxedBoolean(false) => evalRecursive(rhs)
            case BoxedBoolean(true) => BoxedBoolean(true)
            case _ => reportError("type error")
          }
        case TypedAst.BinaryExpression(type_, location, Operator.EQUAL, left, right) =>
          (evalRecursive(left), evalRecursive(right)) match {
            case (BoxedInt(lval), BoxedInt(rval)) => BoxedBoolean(lval == rval)
            case (BoxedByte(lval), BoxedByte(rval)) => BoxedBoolean(lval == rval)
            case (BoxedReal(lval), BoxedReal(rval)) => BoxedBoolean(lval == rval)
            case (BoxedBoolean(lval), BoxedBoolean(rval)) => BoxedBoolean(lval == rval)
            case (BoxedBoolean(lval), ObjectValue(rval:java.lang.Boolean)) => BoxedBoolean(lval == rval.booleanValue())
            case (ObjectValue(lval:java.lang.Boolean), BoxedBoolean(rval)) => BoxedBoolean(lval.booleanValue() == rval)
            case (ObjectValue(lval), ObjectValue(rval)) => BoxedBoolean(lval == rval)
            case (a@RecordValue(_, _), b@RecordValue(_, _)) => BoxedBoolean(a == b)
            case _ => reportError("同じ型同士で比較する必要があります")
          }
        case TypedAst.BinaryExpression(type_, location, Operator.LESS_THAN, left, right) =>
          (evalRecursive(left), evalRecursive(right)) match {
            case (BoxedInt(lval), BoxedInt(rval)) => BoxedBoolean(lval < rval)
            case (BoxedByte(lval), BoxedByte(rval)) => BoxedBoolean(lval < rval)
            case (BoxedReal(lval), BoxedReal(rval)) => BoxedBoolean(lval < rval)
            case _ => reportError("comparation must be done between numeric types")
          }
        case TypedAst.BinaryExpression(type_, location, Operator.GREATER_THAN, left, right) =>
          (evalRecursive(left), evalRecursive(right)) match {
            case (BoxedInt(lval), BoxedInt(rval)) => BoxedBoolean(lval > rval)
            case (BoxedByte(lval), BoxedByte(rval)) => BoxedBoolean(lval > rval)
            case (BoxedReal(lval), BoxedReal(rval)) => BoxedBoolean(lval > rval)
            case _ => reportError("comparation must be done between numeric types")
          }
        case TypedAst.BinaryExpression(type_, location, Operator.LESS_OR_EQUAL, left, right) =>
          (evalRecursive(left), evalRecursive(right)) match {
            case (BoxedInt(lval), BoxedInt(rval)) => BoxedBoolean(lval <= rval)
            case (BoxedByte(lval), BoxedByte(rval)) => BoxedBoolean(lval <= rval)
            case (BoxedReal(lval), BoxedReal(rval)) => BoxedBoolean(lval <= rval)
            case _ => reportError("comparation must be done between numeric types")
          }
        case TypedAst.BinaryExpression(type_, location, Operator.GREATER_EQUAL, left, right) =>
          (evalRecursive(left), evalRecursive(right)) match {
            case (BoxedInt(lval), BoxedInt(rval)) => BoxedBoolean(lval >= rval)
            case (BoxedByte(lval), BoxedByte(rval)) => BoxedBoolean(lval >= rval)
            case (BoxedReal(lval), BoxedReal(rval)) => BoxedBoolean(lval >= rval)
            case _ => reportError("comparation must be done between numeric types")
          }
        case TypedAst.BinaryExpression(type_, location, Operator.ADD, left, right) =>
          (evalRecursive(left), evalRecursive(right)) match{
            case (BoxedByte(lval), BoxedByte(rval)) => BoxedByte((lval + rval).toByte)
            case (BoxedInt(lval), BoxedInt(rval)) => BoxedInt(lval + rval)
            case (ObjectValue(lval:String), rval) => ObjectValue(lval + rval)
            case (lval, ObjectValue(rval:String)) => ObjectValue(lval.toString + rval)
            case (BoxedReal(lval), BoxedReal(rval)) => BoxedReal(lval + rval)
            case _ =>
              reportError(
                s"""|arithmetic operation must be done between the same numeric types:
                    |  left: ${left.type_}, right: ${right.type_}""".stripMargin
              )
          }
        case TypedAst.BinaryExpression(type_, location, Operator.SUBTRACT, left, right) =>
          (evalRecursive(left), evalRecursive(right)) match{
            case (BoxedInt(lval), BoxedInt(rval)) => BoxedInt(lval - rval)
            case (BoxedByte(lval), BoxedByte(rval)) => BoxedByte((lval - rval).toByte)
            case (BoxedReal(lval), BoxedReal(rval)) => BoxedReal(lval - rval)
            case _ => reportError("arithmetic operation must be done between the same numeric types")
          }
        case TypedAst.BinaryExpression(type_, location, Operator.MULTIPLY, left, right) =>
          (evalRecursive(left), evalRecursive(right)) match{
            case (BoxedInt(lval), BoxedInt(rval)) => BoxedInt(lval * rval)
            case (BoxedByte(lval), BoxedByte(rval)) => BoxedByte((lval * rval).toByte)
            case (BoxedReal(lval), BoxedReal(rval)) => BoxedReal(lval * rval)
            case _ => reportError("arithmetic operation must be done between the same numeric types")
          }
        case TypedAst.BinaryExpression(type_, location, Operator.DIVIDE, left, right) =>
          (evalRecursive(left), evalRecursive(right)) match {
            case (BoxedInt(lval), BoxedInt(rval)) => BoxedInt(lval / rval)
            case (BoxedByte(lval), BoxedByte(rval)) => BoxedByte((lval / rval).toByte)
            case (BoxedReal(lval), BoxedReal(rval)) => BoxedReal(lval / rval)
            case _ => reportError("arithmetic operation must be done between the same numeric types")
          }
        case TypedAst.BinaryExpression(type_, location, Operator.AND, left, right) =>
          (evalRecursive(left), evalRecursive(right)) match {
            case (BoxedInt(lval), BoxedInt(rval)) => BoxedInt(lval & rval)
            case (BoxedByte(lval), BoxedByte(rval)) => BoxedByte((lval & rval).toByte)
            case _ => reportError("arithmetic operation must be done between the same numeric types")
          }
        case TypedAst.BinaryExpression(type_, location, Operator.OR, left, right) =>
          (evalRecursive(left), evalRecursive(right)) match {
            case (BoxedInt(lval), BoxedInt(rval)) => BoxedInt(lval | rval)
            case (BoxedByte(lval), BoxedByte(rval)) => BoxedByte((lval | rval).toByte)
            case _ => reportError("arithmetic operation must be done between the same numeric types")
          }
        case TypedAst.BinaryExpression(type_, location, Operator.XOR, left, right) =>
          (evalRecursive(left), evalRecursive(right)) match {
            case (BoxedInt(lval), BoxedInt(rval)) => BoxedInt(lval ^ rval)
            case (BoxedByte(lval), BoxedByte(rval)) => BoxedByte((lval ^ rval).toByte)
            case _ => reportError("arithmetic operation must be done between the same numeric types")
          }
        case TypedAst.MinusOp(type_, location, operand) =>
          evalRecursive(operand) match {
            case BoxedInt(value) => BoxedInt(-value)
            case BoxedByte(value) => BoxedByte((-value).toByte)
            case BoxedReal(value) => BoxedReal(-value)
            case _ => reportError("- cannot be applied to non-integer value")
          }
        case TypedAst.PlusOp(type_, location, operand) =>
          evalRecursive(operand) match {
            case BoxedInt(value) => BoxedInt(value)
            case BoxedByte(value) => BoxedByte(value)
            case BoxedReal(value) => BoxedReal(value)
            case _ => reportError("+ cannot be applied to non-integer value")
          }
        case TypedAst.IntNode(type_, location, value) =>
          BoxedInt(value)
        case TypedAst.StringNode(type_, location, value) =>
          ObjectValue(value)
        case TypedAst.ByteNode(type_, location, value) =>
          BoxedByte(value)
        case TypedAst.RealNode(type_, location, value) =>
          BoxedReal(value)
        case TypedAst.BooleanNode(type_, location, value) =>
          BoxedBoolean(value)
        case TypedAst.ListLiteral(type_, location, elements) =>
          val params = elements.map{e => Value.fromKlassic(evalRecursive(e))}
          val newList = new java.util.ArrayList[Any]
          params.foreach{param =>
            newList.add(param)
          }
          ObjectValue(newList)
        case TypedAst.SetLiteral(type_, location, elements) =>
          val params = elements.map{e => Value.fromKlassic(evalRecursive(e))}
          val newSet = new java.util.HashSet[Any]
          params.foreach{param =>
            newSet.add(param)
          }
          ObjectValue(newSet)
        case TypedAst.DictionaryLiteral(type_, location, elements) =>
          val params = elements.map{ case (k, v) =>
            (Value.fromKlassic(evalRecursive(k)), Value.fromKlassic(evalRecursive(v)))
          }
          val newMap = new java.util.HashMap[Any, Any]
          params.foreach{ case (k, v) =>
            newMap.put(k, v)
          }
          ObjectValue(newMap)
        case TypedAst.Id(type_, location, name) =>
          env(name)
        case TypedAst.Selector(type_, location, module, name) =>
          moduleEnv(module)(name)
        case TypedAst.LetDeclaration(type_, location, vr, optVariableType, value, body, immutable) =>
          env(vr) = evalRecursive(value)
          evalRecursive(body)
        case TypedAst.Assignment(type_, location, vr, value) =>
          env.set(vr, evalRecursive(value))
        case literal@TypedAst.FunctionLiteral(type_, location, _, _, _) =>
          FunctionValue(literal, Some(env))
        case TypedAst.LetFunctionDefinition(type_, location, name, body, expression) =>
          env(name) = FunctionValue(body, Some(env)): Value
          evalRecursive(expression)
        case TypedAst.MethodCall(type_, location, self, name, params) =>
          evalRecursive(self) match {
            case ObjectValue(value) =>
              val paramsArray = params.map{p => evalRecursive(p)}.toArray
              findMethod(value, name, paramsArray) match {
                case UnboxedVersionMethodFound(method) =>
                  println("method: " + method.getName)
                  val actualParams = paramsArray.map{Value.fromKlassic}
                  Value.toKlassic(method.invoke(value, actualParams:_*))
                case BoxedVersionMethodFound(method) =>
                  val actualParams = paramsArray.map{Value.fromKlassic}
                  Value.toKlassic(method.invoke(value, actualParams:_*))
                case NoMethodFound =>
                  throw new IllegalArgumentException(s"${self}.${name}(${params})")
              }
            case otherwise =>
              sys.error(s"cannot reach here: ${otherwise}")
          }
        case TypedAst.ObjectNew(type_, location, className, params) =>
          val paramsArray = params.map{evalRecursive}.toArray
          findConstructor(Class.forName(className), paramsArray) match {
            case UnboxedVersionConstructorFound(constructor) =>
              val actualParams = paramsArray.map{Value.fromKlassic}
              Value.toKlassic(constructor.newInstance(actualParams:_*).asInstanceOf[AnyRef])
            case BoxedVersionConstructorFound(constructor) =>
              val actualParams = paramsArray.map{Value.fromKlassic}
              Value.toKlassic(constructor.newInstance(actualParams:_*).asInstanceOf[AnyRef])
            case NoConstructorFound =>
              throw new IllegalArgumentException(s"new ${className}(${params}) is not found")
          }
        case TypedAst.RecordNew(type_, location, recordName, params) =>
          val paramsList = params.map {
            evalRecursive
          }
          recordEnv.records.get(recordName) match {
            case None => throw new IllegalArgumentException(s"record ${recordName} is not found")
            case Some(argsList) =>
              val members = (argsList zip paramsList).map { case ((n, _), v) => n -> v }
              RecordValue(recordName, members)
          }
        case TypedAst.RecordSelect(type_, location, expression, memberName) =>
          evalRecursive(expression) match {
            case RecordValue(recordName, members) =>
              members.find { case (mname, mtype) => memberName == mname } match {
                case None =>
                  throw new IllegalArgumentException(s"member ${memberName} is not found in record ${recordName}")
                case Some((_, value)) =>
                  value
              }
            case v =>
              throw new IllegalArgumentException(s"value ${v} is not record")
          }
        case TypedAst.MemberSelect(type_, location, expression, moduleName, memberName) =>
          val receiver = evalRecursive(expression)
          BuiltinModuleEnvironment.modules.get(moduleName) match {
            case None =>
              throw new IllegalArgumentException(s"モジュール ${moduleName} が見つかりませんでした")
            case Some(module) =>
              module(memberName)
          }
        case call@TypedAst.FunctionCall(type_, location, function, params) =>
          performFunction(call, env)
        case show@TypedAst.Show(type_, location, expression) =>
          ASTVisualizer.visualize(expression)
        case TypedAst.Casting(type_, location, target, to) =>
          evalRecursive(target)
        case TypedAst.ValueNode(value) =>
          value
        case otherwise@TypedAst.ForeachExpression(type_, location, _, _, _) => sys.error(s"cannot reach here: ${otherwise}")
      }
    }
    evalRecursive(node)
  }

  override final val name: String = "Interpreter"

  override final def process(input: TypedAst.Program, session: InteractiveSession): Value = {
    interpret(input, session)
  }
}
