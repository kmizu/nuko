package com.github.nuko

object Visualizer {
  def main(args: Array[String]): Unit = {
    val ast = Ast.BinaryExpression(NoLocation, Operator.ADD, Ast.IntNode(NoLocation, 1), Ast.IntNode(NoLocation, 2))
    ASTVisualizer.visualize(ast)
  }
}
