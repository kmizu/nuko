package com.github.nuko

sealed abstract class Type(val image: String) {
  def ==>(returnType: Type): Type.TFunction = {
    Type.TFunction(List(this), returnType)
  }
  def raw: String = image
  override def toString: String = image
}
object Type {
  implicit class RichType(args: List[Type]) {
    def ==>(returnType: Type): Type.TFunction = {
      Type.TFunction(args, returnType)
    }
  }

  case class TVariable(name: String) extends Row(name)

  case object TInt extends Type("整数")

  case object TByte extends Type("バイト")

  case object TReal extends Type("小数")

  case object TBoolean extends Type("真偽")

  case object TUnit extends Type("空")

  case object TString extends Type("文字列")

  case object TDynamic extends Type("万物")

  case object TError extends Type("エラー")

  case class TRecordReference(name: String, paramTypes: List[Type]) extends Type(
    s"#${name}${if(paramTypes == Nil) "" else s"<${paramTypes.mkString(", ")}>"}"
  )

  sealed abstract class Row(image: String) extends Type(image)

  case object TRowEmpty extends Row("")

  case class TRowExtend(l: String, t: Type, e: Row) extends Row(
    e match {
      case TVariable(_) => s"${l}: ${t}; ..."
      case _ => s"${l}: ${t}; ${e}"
    }
  )

  case class TRecord(ts: List[TVariable], row: Type) extends Type(s"record { ${row}}")

  case class TFunction(paramTypes: List[Type], returnType: Type) extends Type(s"(${paramTypes.mkString(", ")}) => ${returnType}")

  case class TScheme(svariables: List[TVariable], stype: Type)

  case class TConstructor(name: String, ts: List[Type]) extends Type(name + "<" + ts.mkString(", ") + ">") {
    override def raw: String = name
  }
}
