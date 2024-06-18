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
  describe("substring") {
    val expectations: List[(String, Value)] = List(
      """
        |substring("FOO", 0, 1)
      """.stripMargin -> ObjectValue("F"),
      """
        |substring("FOO", 0, 2)
      """.stripMargin -> ObjectValue("FO"),
      """
        |substring("FOO", 0, 3)
      """.stripMargin -> ObjectValue("FOO"),
      """
        |substring("FOO", 1, 1)
      """.stripMargin -> ObjectValue(""),
      """
        |substring("FOO", 1, 2)
      """.stripMargin -> ObjectValue("O"),
      """
        |substring("FOO", 1, 3)
      """.stripMargin -> ObjectValue("OO")
    )

    expectations.foreach { case (in, expected) =>
      it(s"${in} evaluates to ${expected}") {
        assert(expected == E(in))
      }
    }
  }
  describe("at") {
    val expectations: List[(String, Value)] = List(
      """
        |at("FOO", 0)
      """.stripMargin -> ObjectValue("F"),
      """
        |at("FOO", 1)
      """.stripMargin -> ObjectValue("O"),
      """
        |at("FOO", 2)
      """.stripMargin -> ObjectValue("O")
    )

    expectations.foreach { case (in, expected) =>
      it(s"${in} evaluates to ${expected}") {
        assert(expected == E(in))
      }
    }
  }
  describe("matches") {
    val expectations: List[(String, Value)] = List(
      """
        |matches("FOO", ".*")
      """.stripMargin -> BoxedBoolean(true),
      """
        |matches("FOO", "FOO")
      """.stripMargin -> BoxedBoolean(true),
      """
        |matches("FOO", "FO")
      """.stripMargin -> BoxedBoolean(false),
      """
        |matches("FO", "FOO")
      """.stripMargin -> BoxedBoolean(false)
    )

    expectations.foreach { case (in, expected) =>
      it(s"${in} evaluates to ${expected}") {
        assert(expected == E(in))
      }
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
