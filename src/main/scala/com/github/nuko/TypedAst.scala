package com.github.nuko

import com.github.nuko.Type.{TDynamic, TRecord, TScheme, TVariable}

object TypedAst {
  sealed abstract class TypedNode {
    val location: Location
    val type_   : Type
  }


  type RecordEnvironment = Map[String, TRecord]

  sealed trait IntegerSuffix

  case object ByteSuffix extends IntegerSuffix

  case object ShortSuffix extends IntegerSuffix

  case object LongSuffix extends IntegerSuffix

  sealed trait FloatSuffix

  case object FloatSuffix extends FloatSuffix

  case class Program(location: Location, imports: List[Import], block: Block, records: RecordEnvironment)

  case class Import(location: Location, simpleName: String, fqcn: String)

  case class Block(type_ : Type, location: Location, expressions: List[TypedNode]) extends TypedNode

  case class IfExpression(type_ : Type, location: Location, condition: TypedNode, thenExpression: TypedNode, elseExpression: TypedNode) extends TypedNode

  case class ForeachExpression(type_ : Type, location: Location, name: String, collection: TypedNode, body: TypedNode) extends TypedNode

  case class BinaryExpression(type_ : Type, location: Location, operator: Operator, lhs: TypedNode, rhs: TypedNode) extends TypedNode

  case class WhileExpression(type_ : Type, location: Location, condition: TypedNode, body: TypedNode) extends TypedNode

  case class MinusOp(type_ : Type, location: Location, operand: TypedNode) extends TypedNode

  case class PlusOp(type_ : Type, location: Location, operand: TypedNode) extends TypedNode

  case class StringNode(type_ : Type, location: Location, value: String) extends TypedNode

  case class IntNode(type_ : Type, location: Location, value: BigInt) extends TypedNode

  case class ByteNode(type_ : Type, location: Location, value: Byte) extends TypedNode

  case class BooleanNode(type_ : Type, location: Location, value: Boolean) extends TypedNode

  case class RealNode(type_ : Type, location: Location, value: BigDecimal) extends TypedNode

  case class Id(type_ : Type, location: Location, name: String) extends TypedNode

  case class Selector(type_ : Type, location: Location, module: String, name: String) extends TypedNode

  case class Assignment(type_ : Type, location: Location, variable: String, value: TypedNode) extends TypedNode

  case class LetDeclaration(type_ : Type, location: Location, variable: String, declaredType: Type, value: TypedNode, body: TypedNode, immutable: Boolean) extends TypedNode

  case class FunctionLiteral(type_ : Type, location: Location, params: List[FormalParameterOptional], optionalType: Option[Type], proc: TypedNode) extends TypedNode {
    override def toString: String = s"""${params.mkString(", ")} => ${optionalType.getOrElse("?")}"""
  }

  case class LetFunctionDefinition(type_ : Type, location: Location, name: String, body: FunctionLiteral, expression: TypedNode) extends TypedNode

  case class FunctionCall(type_ : Type, location: Location, func: TypedNode, params: List[TypedNode]) extends TypedNode

  case class Show(type_ : Type, location: Location, expression: Ast.Node) extends TypedNode

  case class ListLiteral(type_ : Type, location: Location, elements: List[TypedNode]) extends TypedNode

  case class SetLiteral(type_ : Type, location: Location, elements: List[TypedNode]) extends TypedNode

  case class DictionaryLiteral(type_ : Type, location: Location, elements: List[(TypedNode, TypedNode)]) extends TypedNode

  case class RecordSelect(type_ : Type, location: Location, expression: TypedNode, member: String) extends TypedNode

  case class MemberSelect(type_ : Type, location: Location, expression: TypedNode, moduleName: String, member: String) extends TypedNode

  case class ObjectNew(type_ : Type, location: Location, className: String, params: List[TypedNode]) extends TypedNode

  case class RecordNew(type_ : Type, location: Location, recordName: String, params: List[TypedNode]) extends TypedNode

  case class MethodCall(type_ : Type, location: Location, self: TypedNode, name: String, params: List[TypedNode]) extends TypedNode

  case class Casting(type_ : Type, location: Location, target: TypedNode, to: Type) extends TypedNode

  case class ValueNode(value: Value) extends TypedNode {
    override val type_   : Type     = TDynamic
    override val location: Location = NoLocation
  }
}