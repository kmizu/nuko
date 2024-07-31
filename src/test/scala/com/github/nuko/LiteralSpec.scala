package com.github.nuko

import java.util.ArrayList

import com.github.nuko.Value.*

class LiteralSpec extends SpecHelper {
  describe("integer literal") {
    val expectations = List[(String, Value)](
      "2" -> BoxedInt(2),
      "+2" -> BoxedInt(+2),
      "-2" -> BoxedInt(-2),
      "1" -> BoxedInt(1),
      "+1" -> BoxedInt(+1),
      "-1" -> BoxedInt(-1),
      "0" -> BoxedInt(0),
      "+0" -> BoxedInt(0),
      "-0" -> BoxedInt(0),
      s"2147483647" -> BoxedInt(2147483647),
    )
    expectations.zipWithIndex.foreach { case ((in, expected), i) =>
      it(s"${in} evaluates to ${expected}") {
        assert(expected == E(in))
      }
    }
  }

  describe("全角の整数と半角の整数が同じ") {
    it("全角０") {
      assert(E("0") == E("０"))
    }
    it("全角１") {
      assert(E("1") == E("１"))
    }
    it("全角２") {
      assert(E("2") == E("２"))
    }
    it("全角１０") {
      assert(E("10") == E("１０"))
    }
    it("全角１２３４５６７８９０") {
      assert(E("1234567890") == E("１２３４５６７８９０"))
    }
  }

  describe("string literal with escape sequence") {
    val expectations = List[(String, Value)](
      """"\r\n"""" -> ObjectValue("\r\n"),
      """"\r"""" -> ObjectValue("\r"),
      """"\n"""" -> ObjectValue("\n"),
      """"\t"""" -> ObjectValue("\t"),
      """"\b"""" -> ObjectValue("\b"),
      """"\f"""" -> ObjectValue("\f"),
      """"\\"""" -> ObjectValue("\\")
    )
    expectations.zipWithIndex.foreach{ case ((in, expected), i) =>
      it(s"${in} evaluates to ${expected}") {
        assert(expected == E(in))
      }
    }
  }
  describe("map literal") {
    val expectations = List[(String, Value)](
      "辞書()" -> ObjectValue(mapOf[String, String]()),
      "辞書(1 -> 2)" -> ObjectValue(mapOf(BigInt(1) -> BigInt(2))),
      """辞書("a" -> "b")""" -> ObjectValue(mapOf("a" -> "b")),
      """辞書("a" -> "b" "c" -> "d")""" -> ObjectValue(mapOf("a" -> "b", "c" -> "d")),
    """辞書("a" -> "b"
         | "c" -> "d")""".stripMargin -> ObjectValue(mapOf("a" -> "b", "c" -> "d"))
    )
    expectations.zipWithIndex.foreach { case ((in, expected), i) =>
      it(s"${in} evaluates to ${expected}") {
        assert(expected == E(in))
      }
    }
  }
}
