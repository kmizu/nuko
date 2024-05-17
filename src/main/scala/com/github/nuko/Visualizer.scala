package com.github.nuko

object Visualizer {
  def main(args: Array[String]): Unit = {
    val ast = Ast.BinaryExpression(NoLocation, Operator.SUBTRACT,
      Ast.BinaryExpression(NoLocation, Operator.ADD, Ast.IntNode(NoLocation, 1), Ast.IntNode(NoLocation, 2)),
      Ast.BinaryExpression(NoLocation, Operator.ADD, Ast.IntNode(NoLocation, 3), Ast.IntNode(NoLocation, 4)),
    )
    ASTVisualizer.visualize(ast)
  }
}
