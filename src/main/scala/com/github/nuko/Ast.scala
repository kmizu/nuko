package com.github.nuko

import com.github.nuko.Type.TVariable

object Ast {
  sealed abstract class Node {
    val location: Location
  }

  sealed trait IntegerSuffix

  case object ByteSuffix extends IntegerSuffix

  case class Program(location: Location, imports: List[Import], block: Block)

  case class Import(location: Location, simpleName: String, fqcn: String)

  case class EnumDeclaration(location: Location, id: String, params: List[Type], constructors: List[DataConstructor]) extends Node

  case class Block(location: Location, expressions: List[Ast.Node]) extends Node

  case class IfExpression(location: Location, condition: Ast.Node, thenExpression: Ast.Node, elseExpression: Ast.Node) extends Node

  case class ForeachExpression(location: Location, name: String, collection: Ast.Node, body: Ast.Node) extends Node

  case class BinaryExpression(location: Location, operator: Operator, lhs: Ast.Node, rhs: Ast.Node) extends Node

  case class TernaryExpression(location: Location, condition: Ast.Node, thenExpression: Ast.Node, elseExpression: Ast.Node) extends Node

  case class WhileExpression(location: Location, condition: Ast.Node, body: Ast.Node) extends Node

  case class MinusOp(location: Location, operand: Ast.Node) extends Node

  case class PlusOp(location: Location, operand: Ast.Node) extends Node

  case class StringNode(location: Location, value: String) extends Node

  case class IntNode(location: Location, value: BigInt) extends Node

  case class ByteNode(location: Location, value: Byte) extends Node

  case class BooleanNode(location: Location, value: Boolean) extends Node

  case class RealNode(location: Location, value: BigDecimal) extends Node

  case class Id(location: Location, name: String) extends Node
  object Id {
    def apply(name: String): Id = Id(NoLocation, name)
  }
  case class Placeholder(location: Location) extends Node
  object Placeholder{
    def apply(): Placeholder= Placeholder(NoLocation)
  }

  case class Selector(location: Location, module: String, name: String) extends Node

  sealed abstract class Assignment extends Ast.Node {
    val location: Location
    val variable: String
    val value: Node
  }

  case class SimpleAssignment(location: Location, variable: String, value: Ast.Node) extends Assignment

  case class PlusAssignment(location: Location, variable: String, value: Ast.Node) extends Assignment

  case class MinusAssignment(location: Location, variable: String, value: Ast.Node) extends Assignment

  case class MultiplicationAssignment(location: Location, variable: String, value: Ast.Node) extends Assignment

  case class DivisionAssignment(location: Location, variable: String, value: Ast.Node) extends Assignment

  case class ValDeclaration(location: Location, variable: String, type_ : Option[Type], value: Ast.Node, immutable: Boolean) extends Node

  case class EnumIn(location: Location, variant: EnumDeclaration, body: Ast.Node) extends Node

  case class Let(location: Location, variable: String, type_ : Option[Type], value: Ast.Node, body: Ast.Node, immutable: Boolean) extends Node
  object Let {
    def apply(variable: String, type_ :Option[Type], value: Ast.Node, body: Ast.Node): Let = {
      Let(NoLocation, variable, type_, value, body, true)
    }
  }

  case class Lambda(location: Location, params: List[FormalParameterOptional], optionalType: Option[Type], body: Ast.Node) extends Node
  object Lambda {
    def apply(params: List[String], body: Ast.Node): Lambda = {
      Lambda(NoLocation, params.map{ case name => FormalParameterOptional(name, None)}, None, body)
    }
  }

  case class FunctionDefinition(location: Location, name: String, body: Lambda) extends Node

  case class MethodDefinition(location: Location, name: String, body: Lambda) extends Node

  case class LetRec(location: Location, name: String, function: Lambda, body: Ast.Node) extends Node
  object LetRec {
    def apply(name: String, function: Lambda, body: Ast.Node): LetRec = {
      LetRec(NoLocation, name, function, body)
    }
  }

  case class FunctionCall(location: Location, func: Ast.Node, params: List[Ast.Node]) extends Node

  case class ListLiteral(location: Location, elements: List[Ast.Node]) extends Node

  case class SetLiteral(location: Location, elements: List[Ast.Node]) extends Node

  case class DictionaryLiteral(location: Location, elements: List[(Ast.Node, Ast.Node)]) extends Node

  case class ObjectNew(location: Location, className: String, params: List[Ast.Node]) extends Node

  case class MethodCall(location: Location, self: Ast.Node, name: String, params: List[Ast.Node]) extends Node

  case class Casting(location: Location, target: Ast.Node, to: Type) extends Node

  case class Show(location: Location, target: Ast.Node) extends Node
}