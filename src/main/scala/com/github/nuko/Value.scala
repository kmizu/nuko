package com.github.nuko

import com.github.nuko.TypedAst.TypedNode

sealed abstract class Value
case class BoxedByte(value: Byte) extends Value {
  override def toString = value.toString
}
case class BoxedInt(value: BigInt) extends Value {
  override def toString = value.toString
}
case class BoxedBoolean(value: Boolean) extends Value {
  override def toString = value.toString
}
case class BoxedReal(value: BigDecimal) extends Value {
  override def toString = value.toString
}
case class FunctionValue(value: TypedAst.FunctionLiteral, environment: Option[RuntimeEnvironment]) extends Value {
  override def toString = s"<function value>"
}
case class NativeFunctionValue(body: PartialFunction[List[Value], Value]) extends Value {
  override def toString = s"<native function>"
}
case object UnitValue extends Value {
  override def toString = "()"
}
case class ObjectValue(value: AnyRef) extends Value {
  override def toString = if(value eq null) "null" else value.toString
}
case class RecordValue(name: String, members: List[(String, Value)]) extends Value {
  override def toString =
    s"""| record ${name} {
        | ${members.map{ case (n, v) => s"\t${n} = ${v}"}.mkString("\n")}
        | }
    """.stripMargin
}
case class EnumValue(tag: String, items: List[Value]) extends Value {
  override def toString: String = {
    s"${tag}(${items.mkString(", ")})"
  }
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
