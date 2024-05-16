package com.github.nuko

import javax.swing._
import javax.swing.tree._
import java.awt._

object ASTVisualizer {
  def visualize(tree: Ast.Node): Value = {
    // メインフレームの作成
    val frame = new JFrame("AST Visualizer")
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
    frame.setSize(800, 600)

    // ASTのルートノードをJTreeのルートノードに変換
    val rootNode = createTreeNode(tree)

    // JTreeの作成
    val treeModel = new DefaultTreeModel(rootNode)
    val jTree = new JTree(treeModel)

    // JTreeをスクロールペインに追加
    val scrollPane = new JScrollPane(jTree)
    frame.add(scrollPane)

    // フレームを表示
    frame.setVisible(true)

    UnitValue
  }

  // ASTノードをJTreeノードに変換するヘルパー関数
  private def createTreeNode(node: Ast.Node): DefaultMutableTreeNode = {
    val treeNode = new DefaultMutableTreeNode(nodeToString(node))
    node match {
      case Ast.Block(_, expressions) =>
        expressions.foreach(expr => treeNode.add(createTreeNode(expr)))
      case Ast.IfExpression(_, condition, thenExpr, elseExpr) =>
        treeNode.add(createTreeNode(condition))
        treeNode.add(createTreeNode(thenExpr))
        treeNode.add(createTreeNode(elseExpr))
      case Ast.ForeachExpression(_, name, collection, body) =>
        treeNode.add(new DefaultMutableTreeNode(s"Variable: $name"))
        treeNode.add(createTreeNode(collection))
        treeNode.add(createTreeNode(body))
      case Ast.BinaryExpression(_, operator, lhs, rhs) =>
        treeNode.add(new DefaultMutableTreeNode(s"Operator: $operator"))
        treeNode.add(createTreeNode(lhs))
        treeNode.add(createTreeNode(rhs))
      case Ast.TernaryExpression(_, condition, thenExpr, elseExpr) =>
        treeNode.add(createTreeNode(condition))
        treeNode.add(createTreeNode(thenExpr))
        treeNode.add(createTreeNode(elseExpr))
      case Ast.WhileExpression(_, condition, body) =>
        treeNode.add(createTreeNode(condition))
        treeNode.add(createTreeNode(body))
      case Ast.MinusOp(_, operand) =>
        treeNode.add(createTreeNode(operand))
      case Ast.PlusOp(_, operand) =>
        treeNode.add(createTreeNode(operand))
      case Ast.StringNode(_, value) =>
        treeNode.add(new DefaultMutableTreeNode(s"Value: $value"))
      case Ast.IntNode(_, value) =>
        treeNode.add(new DefaultMutableTreeNode(s"Value: $value"))
      case Ast.ByteNode(_, value) =>
        treeNode.add(new DefaultMutableTreeNode(s"Value: $value"))
      case Ast.BooleanNode(_, value) =>
        treeNode.add(new DefaultMutableTreeNode(s"Value: $value"))
      case Ast.DoubleNode(_, value) =>
        treeNode.add(new DefaultMutableTreeNode(s"Value: $value"))
      case Ast.Id(_, name) =>
        treeNode.add(new DefaultMutableTreeNode(s"Name: $name"))
      case Ast.Placeholder(_) =>
        treeNode.add(new DefaultMutableTreeNode("Placeholder"))
      case Ast.Selector(_, module, name) =>
        treeNode.add(new DefaultMutableTreeNode(s"Module: $module"))
        treeNode.add(new DefaultMutableTreeNode(s"Name: $name"))
      case Ast.SimpleAssignment(_, variable, value) =>
        treeNode.add(new DefaultMutableTreeNode(s"Variable: $variable"))
        treeNode.add(createTreeNode(value))
      case Ast.ValDeclaration(_, variable, _, value, immutable) =>
        treeNode.add(new DefaultMutableTreeNode(s"Variable: $variable"))
        treeNode.add(new DefaultMutableTreeNode(s"Immutable: $immutable"))
        treeNode.add(createTreeNode(value))
      case Ast.Lambda(_, params, _, body) =>
        params.foreach(param => treeNode.add(new DefaultMutableTreeNode(s"Param: $param")))
        treeNode.add(createTreeNode(body))
      case Ast.FunctionDefinition(_, name, body) =>
        treeNode.add(new DefaultMutableTreeNode(s"Name: $name"))
        treeNode.add(createTreeNode(body))
      case Ast.FunctionCall(_, func, params) =>
        treeNode.add(createTreeNode(func))
        params.foreach(param => treeNode.add(createTreeNode(param)))
      case Ast.ListLiteral(_, elements) =>
        elements.foreach(elem => treeNode.add(createTreeNode(elem)))
      case Ast.SetLiteral(_, elements) =>
        elements.foreach(elem => treeNode.add(createTreeNode(elem)))
      case Ast.MapLiteral(_, elements) =>
        elements.foreach { case (key, value) =>
          val entryNode = new DefaultMutableTreeNode("Entry")
          entryNode.add(createTreeNode(key))
          entryNode.add(createTreeNode(value))
          treeNode.add(entryNode)
        }
      case Ast.ObjectNew(_, className, params) =>
        treeNode.add(new DefaultMutableTreeNode(s"Class: $className"))
        params.foreach(param => treeNode.add(createTreeNode(param)))
      case Ast.MethodCall(_, self, name, params) =>
        treeNode.add(createTreeNode(self))
        treeNode.add(new DefaultMutableTreeNode(s"Method: $name"))
        params.foreach(param => treeNode.add(createTreeNode(param)))
      case Ast.Casting(_, target, to) =>
        treeNode.add(createTreeNode(target))
        treeNode.add(new DefaultMutableTreeNode(s"To: $to"))
      case _ =>
      // 他のノードタイプはここで処理
    }
    treeNode
  }

  // ASTノードを文字列に変換するヘルパー関数
  private def nodeToString(node: Ast.Node): String = {
    node match {
      case Ast.Block(_, _) => "Block"
      case Ast.IfExpression(_, _, _, _) => "IfExpression"
      case Ast.ForeachExpression(_, name, _, _) => s"ForeachExpression: $name"
      case Ast.BinaryExpression(_, operator, _, _) => s"BinaryExpression: $operator"
      case Ast.TernaryExpression(_, _, _, _) => "TernaryExpression"
      case Ast.WhileExpression(_, _, _) => "WhileExpression"
      case Ast.MinusOp(_, _) => "MinusOp"
      case Ast.PlusOp(_, _) => "PlusOp"
      case Ast.StringNode(_, value) => s"StringNode: $value"
      case Ast.IntNode(_, value) => s"IntNode: $value"
      case Ast.ByteNode(_, value) => s"ByteNode: $value"
      case Ast.BooleanNode(_, value) => s"BooleanNode: $value"
      case Ast.DoubleNode(_, value) => s"DoubleNode: $value"
      case Ast.Id(_, name) => s"Id: $name"
      case Ast.Placeholder(_) => "Placeholder"
      case Ast.Selector(_, module, name) => s"Selector: $module.$name"
      case Ast.SimpleAssignment(_, variable, _) => s"SimpleAssignment: $variable"
      case Ast.ValDeclaration(_, variable, _, _, immutable) => s"ValDeclaration: $variable (immutable: $immutable)"
      case Ast.Lambda(_, params, _, _) => s"Lambda: ${params.mkString(", ")}"
      case Ast.FunctionDefinition(_, name, _) => s"FunctionDefinition: $name"
      case Ast.FunctionCall(_, _, _) => "FunctionCall"
      case Ast.ListLiteral(_, _) => "ListLiteral"
      case Ast.SetLiteral(_, _) => "SetLiteral"
      case Ast.MapLiteral(_, _) => "MapLiteral"
      case Ast.ObjectNew(_, className, _) => s"ObjectNew: $className"
      case Ast.MethodCall(_, _, name, _) => s"MethodCall: $name"
      case Ast.Casting(_, _, to) => s"Casting to: $to"
      case _ => "Unknown Node"
    }
  }
}