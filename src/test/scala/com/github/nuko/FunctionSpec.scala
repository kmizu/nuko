package com.github.nuko

class FunctionSpec extends SpecHelper {
  describe("切り上げが機能する") {
    val expectations: List[(String, Value)] = List(
      """
        |切り上げ(2.5)
      """.stripMargin -> BoxedInt(3),
      """
        |切り上げ(2.0)
      """.stripMargin -> BoxedInt(2),
      """
        |切り上げ(0.5)
      """.stripMargin -> BoxedInt(1),
      """
        |切り上げ(-0.5)
      """.stripMargin -> BoxedInt(0),
      """
        |切り上げ(-1.5)
      """.stripMargin -> BoxedInt(-1)
    )

    expectations.foreach { case (in, expected) =>
      it(s"${in} evaluates to ${expected}") {
        assert(expected == E(in))
      }
    }
  }
  describe("整数への変換") {
    val expectations: List[(String, Value)] = List(
      """
        |整数(2.5)
      """.stripMargin -> BoxedInt(2),
      """
        |整数(2.0)
      """.stripMargin -> BoxedInt(2),
      """
        |整数(0.5)
      """.stripMargin -> BoxedInt(0),
      """
        |整数(-0.5)
      """.stripMargin -> BoxedInt(0),
      """
        |整数(-1.5)
      """.stripMargin -> BoxedInt(-1)
    )

    expectations.foreach { case (in, expected) =>
      it(s"${in} evaluates to ${expected}") {
        assert(expected == E(in))
      }
    }
  }
  describe("小数への変換") {
    val expectations: List[(String, Value)] = List(
      """
        |小数(3)
      """.stripMargin -> BoxedReal(3.0),
      """
        |小数(2)
      """.stripMargin -> BoxedReal(2.0),
      """
        |小数(1)
      """.stripMargin -> BoxedReal(1.0),
      """
        |小数(0)
      """.stripMargin -> BoxedReal(0.0),
      """
        |小数(-1)
      """.stripMargin -> BoxedReal(-1.0)
    )

    expectations.foreach { case (in, expected) =>
      it(s"${in} evaluates to ${expected}") {
        assert(expected == E(in))
      }
    }
  }
  describe("切り捨てが機能する") {
    val expectations: List[(String, Value)] = List(
      """
        |切り捨て(2.5)
      """.stripMargin -> BoxedInt(2),
      """
        |切り捨て(2.0)
      """.stripMargin -> BoxedInt(2),
      """
        |切り捨て(0.5)
      """.stripMargin -> BoxedInt(0),
      """
        |切り捨て(-0.5)
      """.stripMargin -> BoxedInt(0),
      """
        |切り捨て(-1.5)
      """.stripMargin -> BoxedInt(-1)
    )

    expectations.foreach { case (in, expected) =>
      it(s"${in} evaluates to ${expected}") {
        assert(expected == E(in))
      }
    }
  }
  describe("絶対値が計算できる") {
    val expectations: List[(String, Value)] = List(
      """
        |絶対値(2.5)
      """.stripMargin -> BoxedReal(2.5),
      """
        |絶対値(1.0)
      """.stripMargin -> BoxedReal(1.0),
      """
        |絶対値(0.0)
      """.stripMargin -> BoxedReal(0.0),
      """
        |絶対値(-0.5)
      """.stripMargin -> BoxedReal(0.5),
      """
        |絶対値(-1.5)
      """.stripMargin -> BoxedReal(1.5)
    )

    expectations.foreach { case (in, expected) =>
      it(s"${in} evaluates to ${expected}") {
        assert(expected == E(in))
      }
    }
  }
  describe("部分文字列を求める") {
    it("FOOのインデックス0から1まで") {
      assert(ObjectValue("F") == E("""部分文字列("FOO", 0, 1)"""))
    }
    it("FOOのインデックス0から2まで") {
      assert(ObjectValue("FO") == E("""部分文字列("FOO", 0, 2)"""))
    }
    it("FOOのインデックス0から3まで") {
      assert(ObjectValue("FOO") == E("""部分文字列("FOO", 0, 3)"""))
    }
    it("FOOのインデックス1から1まで") {
      assert(ObjectValue("") == E("""部分文字列("FOO", 1, 1)"""))
    }
    it("FOOのインデックス1から2まで") {
      assert(ObjectValue("O") == E("""部分文字列("FOO", 1, 2)"""))
    }
    it("FOOのインデックス1から3まで") {
      assert(ObjectValue("OO") == E("""部分文字列("FOO", 1, 3)"""))
    }
  }
  describe("文字列から添字を指定して文字を取得できる") {
    it("FOOのインデックス0") {
      assert(ObjectValue("F") == E("""文字を取得("FOO", 0)"""))
    }
    it("FOOのインデックス1") {
      assert(ObjectValue("O") == E("""文字を取得("FOO", 1)"""))
    }
    it("FOOのインデックス2") {
      assert(ObjectValue("O") == E("""文字を取得("FOO", 2)"""))
    }
  }
  describe("正規表現を使ったマッチができる") {
    it("正規表現 .*") {
      assert(BoxedBoolean(true) == E("""マッチする("FOO", ".*")"""))
    }
    it("正規表現 FOO") {
      assert(BoxedBoolean(true) == E("""マッチする("FOO", "FOO")"""))
    }
    it("正規表現 FO") {
      assert(BoxedBoolean(false) == E("""マッチする("FOO", "FO")"""))
    }
  }
  describe("平方根が計算できる") {
    val expectations: List[(String, Value)] = List(
      """
        |平方根(4.0)
      """.stripMargin -> BoxedReal(2.0),
      """
        |平方根(9.0)
      """.stripMargin -> BoxedReal(3.0),
      """
        |平方根(2.0)
      """.stripMargin -> BoxedReal(1.4142135623730951),
      """
        |平方根(1.0)
      """.stripMargin -> BoxedReal(1.0)
    )

    expectations.foreach { case (in, expected) =>
      it(s"${in} evaluates to ${expected}") {
        assert(expected == E(in))
      }
    }
  }
}
