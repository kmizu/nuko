package com.github.nuko

import javax.swing._
import javax.swing.tree._
import java.awt._
import java.awt.event._
import javax.swing.plaf.metal.MetalLookAndFeel

object ASTVisualizer {
  def visualize(tree: Ast.Node): Value = {
    // Metal Look and Feelの設定
    try {
      UIManager.setLookAndFeel(new MetalLookAndFeel)
    } catch {
      case e: UnsupportedLookAndFeelException => e.printStackTrace()
    }

    // メインフレームの作成
    val frame = new JFrame("抽象構文木の可視化")
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
    frame.setSize(800, 600)

    // アイコンの設定
    val icon = new ImageIcon("path/to/icon.png")
    frame.setIconImage(icon.getImage)

    // ASTのルートノードをJTreeのルートノードに変換
    val rootNode = createTreeNode(tree)

    // JTreeの作成
    val treeModel = new DefaultTreeModel(rootNode)
    val jTree = new JTree(treeModel)

    // ノードのカスタムレンダリング
    jTree.setCellRenderer(new DefaultTreeCellRenderer {
      override def getTreeCellRendererComponent(tree: JTree, value: Any, sel: Boolean, expanded: Boolean, leaf: Boolean, row: Int, hasFocus: Boolean): Component = {
        val c = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus).asInstanceOf[JLabel]
        value match {
          case node: DefaultMutableTreeNode =>
            if (leaf) {
              c.setForeground(new Color(34, 139, 34)) // 葉ノードは緑色
            } else {
              c.setForeground(new Color(70, 130, 180)) // 内部ノードは青色
            }
            c.setFont(new Font("Serif", Font.BOLD, 14)) // フォントを変更
          case _ =>
        }
        c
      }
    })

    // ツリーパネルのスタイリング
    val scrollPane = new JScrollPane(jTree)
    scrollPane.setBackground(new Color(245, 245, 245))
    scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10))
    frame.add(scrollPane)

    // フレームを表示
    frame.setVisible(true)

    UnitValue
  }

  // ASTノードをJTreeノードに変換するヘルパー関数
  private def createTreeNode(node: Ast.Node): DefaultMutableTreeNode = {
    node match {
      case Ast.Block(_, expressions) =>
        val treeNode = new DefaultMutableTreeNode(nodeToString(node))
        expressions.foreach(expr => treeNode.add(createTreeNode(expr)))
        treeNode
      case Ast.IfExpression(_, condition, thenExpr, elseExpr) =>
        val treeNode = new DefaultMutableTreeNode(nodeToString(node))
        treeNode.add(createTreeNode(condition))
        treeNode.add(createTreeNode(thenExpr))
        treeNode.add(createTreeNode(elseExpr))
        treeNode
      case Ast.ForeachExpression(_, name, collection, body) =>
        val treeNode = new DefaultMutableTreeNode(nodeToString(node))
        treeNode.add(new DefaultMutableTreeNode(s"Variable: $name"))
        treeNode.add(createTreeNode(collection))
        treeNode.add(createTreeNode(body))
        treeNode
      case Ast.BinaryExpression(_, operator, lhs, rhs) =>
        val treeNode = new DefaultMutableTreeNode(nodeToString(node))
        treeNode.add(createTreeNode(lhs))
        treeNode.add(createTreeNode(rhs))
        treeNode
      case Ast.TernaryExpression(_, condition, thenExpr, elseExpr) =>
        val treeNode = new DefaultMutableTreeNode(nodeToString(node))
        treeNode.add(createTreeNode(condition))
        treeNode.add(createTreeNode(thenExpr))
        treeNode.add(createTreeNode(elseExpr))
        treeNode
      case Ast.WhileExpression(_, condition, body) =>
        val treeNode = new DefaultMutableTreeNode(nodeToString(node))
        treeNode.add(createTreeNode(condition))
        treeNode.add(createTreeNode(body))
        treeNode
      case Ast.MinusOp(_, operand) =>
        val treeNode = new DefaultMutableTreeNode(nodeToString(node))
        treeNode.add(createTreeNode(operand))
        treeNode
      case Ast.PlusOp(_, operand) =>
        val treeNode = new DefaultMutableTreeNode(nodeToString(node))
        treeNode.add(createTreeNode(operand))
        treeNode
      case Ast.StringNode(_, value) =>
        val treeNode = new DefaultMutableTreeNode(nodeToString(node))
        treeNode.add(new DefaultMutableTreeNode(s"Value: $value"))
        treeNode
      case Ast.IntNode(_, value) =>
        new DefaultMutableTreeNode(nodeToString(node))
      case Ast.ByteNode(_, value) =>
        new DefaultMutableTreeNode(nodeToString(node))
      case Ast.BooleanNode(_, value) =>
        new DefaultMutableTreeNode(nodeToString(node))
      case Ast.DoubleNode(_, value) =>
        new DefaultMutableTreeNode(nodeToString(node))
      case Ast.Id(_, name) =>
        new DefaultMutableTreeNode(nodeToString(node))
      case Ast.Placeholder(_) =>
        val treeNode = new DefaultMutableTreeNode(nodeToString(node))
        treeNode.add(new DefaultMutableTreeNode("Placeholder"))
        treeNode
      case Ast.Selector(_, module, name) =>
        val treeNode = new DefaultMutableTreeNode(nodeToString(node))
        treeNode.add(new DefaultMutableTreeNode(s"Module: $module"))
        treeNode.add(new DefaultMutableTreeNode(s"Name: $name"))
        treeNode
      case Ast.SimpleAssignment(_, variable, value) =>
        val treeNode = new DefaultMutableTreeNode(nodeToString(node))
        treeNode.add(new DefaultMutableTreeNode(s"Variable: $variable"))
        treeNode.add(createTreeNode(value))
        treeNode
      case Ast.ValDeclaration(_, variable, _, value, immutable) =>
        val treeNode = new DefaultMutableTreeNode(nodeToString(node))
        treeNode.add(new DefaultMutableTreeNode(s"Variable: $variable"))
        treeNode.add(new DefaultMutableTreeNode(s"Immutable: $immutable"))
        treeNode.add(createTreeNode(value))
        treeNode
      case Ast.Lambda(_, params, _, body) =>
        val treeNode = new DefaultMutableTreeNode(nodeToString(node))
        params.foreach(param => treeNode.add(new DefaultMutableTreeNode(s"Param: $param")))
        treeNode.add(createTreeNode(body))
        treeNode
      case Ast.FunctionDefinition(_, name, body) =>
        val treeNode = new DefaultMutableTreeNode(nodeToString(node))
        treeNode.add(new DefaultMutableTreeNode(s"Name: $name"))
        treeNode.add(createTreeNode(body))
        treeNode
      case Ast.FunctionCall(_, func, params) =>
        val treeNode = new DefaultMutableTreeNode(nodeToString(node))
        treeNode.add(createTreeNode(func))
        params.foreach(param => treeNode.add(createTreeNode(param)))
        treeNode
      case Ast.ListLiteral(_, elements) =>
        val treeNode = new DefaultMutableTreeNode(nodeToString(node))
        elements.foreach(elem => treeNode.add(createTreeNode(elem)))
        treeNode
      case Ast.SetLiteral(_, elements) =>
        val treeNode = new DefaultMutableTreeNode(nodeToString(node))
        elements.foreach(elem => treeNode.add(createTreeNode(elem)))
        treeNode
      case Ast.MapLiteral(_, elements) =>
        val treeNode = new DefaultMutableTreeNode(nodeToString(node))
        elements.foreach { case (key, value) =>
          val entryNode = new DefaultMutableTreeNode("Entry")
          entryNode.add(createTreeNode(key))
          entryNode.add(createTreeNode(value))
          treeNode.add(entryNode)
        }
        treeNode
      case Ast.ObjectNew(_, className, params) =>
        val treeNode = new DefaultMutableTreeNode(nodeToString(node))
        treeNode.add(new DefaultMutableTreeNode(s"Class: $className"))
        params.foreach(param => treeNode.add(createTreeNode(param)))
        treeNode
      case Ast.MethodCall(_, self, name, params) =>
        val treeNode = new DefaultMutableTreeNode(nodeToString(node))
        treeNode.add(createTreeNode(self))
        treeNode.add(new DefaultMutableTreeNode(s"Method: $name"))
        params.foreach(param => treeNode.add(createTreeNode(param)))
        treeNode
      case Ast.Casting(_, target, to) =>
        new DefaultMutableTreeNode(nodeToString(node))
      case n =>
        throw new RuntimeException(s"Unknown Node Type: $n")
    }
  }

  // ASTノードを文字列に変換するヘルパー関数
  private def nodeToString(node: Ast.Node): String = {
    node match {
      case Ast.Block(_, _) => "ブロック"
      case Ast.IfExpression(_, _, _, _) => "もし"
      case Ast.ForeachExpression(_, name, _, _) => s"ForeachExpression: $name"
      case Ast.BinaryExpression(_, operator, _, _) => s"${operator.descriptor}"
      case Ast.TernaryExpression(_, _, _, _) => "TernaryExpression"
      case Ast.WhileExpression(_, _, _) => "繰り返す"
      case Ast.MinusOp(_, _) => "+"
      case Ast.PlusOp(_, _) => "-"
      case Ast.StringNode(_, value) => s"\"$value\""
      case Ast.IntNode(_, value) => s"整数($value)"
      case Ast.ByteNode(_, value) => s"バイト($value)"
      case Ast.BooleanNode(_, value) => s"真偽値(${if(value) "真" else "偽"})"
      case Ast.DoubleNode(_, value) => s"小数($value)"
      case Ast.Id(_, name) => name
      case Ast.Placeholder(_) => "Placeholder"
      case Ast.Selector(_, module, name) => s"Selector: $module.$name"
      case Ast.SimpleAssignment(_, variable, _) => s"SimpleAssignment: $variable"
      case Ast.ValDeclaration(_, variable, _, _, immutable) => s"ValDeclaration: $variable (immutable: $immutable)"
      case Ast.Lambda(_, params, _, _) => s"Lambda: ${params.mkString(", ")}"
      case Ast.FunctionDefinition(_, name, _) => s"FunctionDefinition: $name"
      case Ast.FunctionCall(_, _, _) => "関数呼び出し"
      case Ast.ListLiteral(_, _) => "リスト"
      case Ast.SetLiteral(_, _) => "集合"
      case Ast.MapLiteral(_, _) => "辞書"
      case Ast.ObjectNew(_, className, _) => s"ObjectNew: $className"
      case Ast.MethodCall(_, _, name, _) => s"メソッド呼び出し: $name"
      case Ast.Casting(_, _, to) => s"キャスト($to)"
      case _ => "Unknown Node"
    }
  }
}