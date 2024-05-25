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
      "2.0" -> BoxedDouble(2.0),
      "2.5" -> BoxedDouble(2.5),
      "+0.0" -> BoxedDouble(+0.0),
      "-0.0" -> BoxedDouble(-0.0),
      "0.1" -> BoxedDouble(+0.1),
      "-0.1" -> BoxedDouble(-0.1)
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
  describe("list literal") {
    val expectations = List[(String, Value)](
      "[]" -> ObjectValue(listOf[Any]()),
      "[1]" -> ObjectValue(listOf(BigInt(1))),
      """["a"]""" -> ObjectValue(listOf("a")),
      "[1, 2]" -> ObjectValue(listOf(BigInt(1), BigInt(2))),
      """|[1
        | 2]
      """.stripMargin -> ObjectValue(listOf(BigInt(1), BigInt(2))),
      """|[1,
        |
        | 2]
      """.stripMargin -> ObjectValue(listOf(BigInt(1), BigInt(2))),
      """|[1
        |
        | 2]
      """.stripMargin -> ObjectValue(listOf(BigInt(1), BigInt(2))),
      """|[1 +
        |
        | 2]
      """.stripMargin -> ObjectValue(listOf(BigInt(3))),
      """|[1, 2
        | 3]
      """.stripMargin -> ObjectValue(listOf(BigInt(1), BigInt(2), BigInt(3))),
      """|[1 2
         | 3 4]
      """.stripMargin -> ObjectValue(listOf(BigInt(1), BigInt(2), BigInt(3), BigInt(4))),
      """| [[1 2]
         |  [3 4]]
      """.stripMargin -> ObjectValue(
        listOf(
          listOf(BigInt(1), BigInt(2)),
          listOf(BigInt(3), BigInt(4))
        )
      )
    )
    expectations.zipWithIndex.foreach { case ((in, expected), i) =>
      it(s"${in} evaluates to ${expected}") {
        assert(expected == E(in))
      }
    }
  }
  describe("map literal") {
    val expectations = List[(String, Value)](
      "辞書[]" -> ObjectValue(mapOf[String, String]()),
      "辞書[1 : 2]" -> ObjectValue(mapOf(BigInt(1) -> BigInt(2))),
      """辞書["a":"b"]""" -> ObjectValue(mapOf("a" -> "b")),
      """辞書["a":"b" "c":"d"]""" -> ObjectValue(mapOf("a" -> "b", "c" -> "d")),
    """辞書["a":"b"
      | "c":"d"]""".stripMargin -> ObjectValue(mapOf("a" -> "b", "c" -> "d"))
    )
    expectations.zipWithIndex.foreach { case ((in, expected), i) =>
      it(s"${in} evaluates to ${expected}") {
        assert(expected == E(in))
      }
    }
  }
}
