package com.github.nuko

import com.github.nuko.TypedAst.TypedNode

enum Value {
  override def toString = this match {
    case BoxedByte(value) => value.toString
    case BoxedInt(value) => value.toString()
    case BoxedBoolean(value) => value.toString
    case BoxedReal(value) => value.toString()
    case FunctionValue(value: TypedAst.FunctionLiteral, environment: Option[RuntimeEnvironment]) => value.toString
    case NativeFunctionValue(body: PartialFunction[List[Value], Value]) => s"<native function>"
    case UnitValue => "()"
    case ObjectValue(value: AnyRef) => if(value eq null) "null" else value.toString
    case RecordValue(name: String, members: List[(String, Value)]) =>
      s"""| record ${name} {
          | ${members.map{ case (n, v) => s"\t${n} = ${v}"}.mkString("\n")}
          | }
     """.stripMargin
    case EnumValue(tag, items) => s"${tag}(${items.mkString(", ")})"
  }
  case BoxedByte(value: Byte)
  case BoxedInt(value: BigInt)
  case BoxedBoolean(value: Boolean)
  case BoxedReal(value: BigDecimal)
  case FunctionValue(value: TypedAst.FunctionLiteral, environment: Option[RuntimeEnvironment])
  case NativeFunctionValue(body: PartialFunction[List[Value], Value])
  case UnitValue
  case ObjectValue(value: AnyRef)
  case RecordValue(name: String, members: List[(String, Value)])
  case EnumValue(tag: String, items: List[Value])
}
object Value {
  def classOfValue(value: Value): java.lang.Class[_]= value match {
    case BoxedBoolean(v) => classOf[Boolean]
    case BoxedByte(v) => classOf[Byte]
    case BoxedInt(v) => classOf[Int]
    case BoxedReal(v) => classOf[Double]
    case ObjectValue(v) => v.getClass
    case otherwise => otherwise.getClass
  }

  def boxedClassOfValue(value: Value): java.lang.Class[_]= value match {
    case BoxedBoolean(v) => classOf[java.lang.Boolean]
    case BoxedByte(v) => classOf[java.lang.Byte]
    case BoxedInt(v) => classOf[java.lang.Integer]
    case BoxedReal(v) => classOf[java.lang.Double]
    case ObjectValue(v) => v.getClass
    case otherwise => otherwise.getClass
  }

  def boxedClassesOfValues(values: Array[Value]): Array[java.lang.Class[_]] = values.map(boxedClassOfValue)

  def classesOfValues(values: Array[Value]):  Array[java.lang.Class[_]] = values.map(classOfValue)

  def fromKlassic(value: Value): AnyRef = value match {
    case BoxedBoolean(v) => java.lang.Boolean.valueOf(v)
    case BoxedByte(v) => java.lang.Byte.valueOf(v)
    case BoxedInt(v) => v
    case BoxedReal(v) => v
    case ObjectValue(v) => v
    case UnitValue => UnitValue
    case otherwise => otherwise
  }

  def toKlassic(value: AnyRef): Value = value match {
    case v:java.lang.Boolean => BoxedBoolean(v.booleanValue())
    case v:java.lang.Byte => BoxedByte(v.byteValue())
    case v:BigInt => BoxedInt(v)
    case v:BigDecimal => BoxedReal(v)
    case v:java.lang.Double => BoxedReal(v.doubleValue())
    case UnitValue => UnitValue
    case otherwise => ObjectValue(otherwise)
  }
}