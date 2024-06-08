package com.github.nuko

import java.util.ArrayList

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

  describe("double literal") {
    val expectations = List[(String, Value)](
      "2.0" -> BoxedReal(2.0),
      "2.5" -> BoxedReal(2.5),
      "+0.0" -> BoxedReal(+0.0),
      "-0.0" -> BoxedReal(-0.0),
      "0.1" -> BoxedReal(+0.1),
      "-0.1" -> BoxedReal(-0.1)
    )

    expectations.zipWithIndex.foreach{ case ((in, expected), i) =>
      it(s"${in} evaluates to ${expected}") {
        assert(expected == E(in))
      }
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
      "辞書[]" -> ObjectValue(mapOf[String, String]()),
      "辞書[1 -> 2]" -> ObjectValue(mapOf(BigInt(1) -> BigInt(2))),
      """辞書["a" -> "b"]""" -> ObjectValue(mapOf("a" -> "b")),
      """辞書["a" -> "b" "c" -> "d"]""" -> ObjectValue(mapOf("a" -> "b", "c" -> "d")),
    """辞書["a" -> "b"
         | "c" -> "d"]""".stripMargin -> ObjectValue(mapOf("a" -> "b", "c" -> "d"))
    )
    expectations.zipWithIndex.foreach { case ((in, expected), i) =>
      it(s"${in} evaluates to ${expected}") {
        assert(expected == E(in))
      }
    }
  }
}
