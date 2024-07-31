package com.github.nuko

import com.github.nuko.Value.*

class ListLiteralSpec extends SpecHelper {
  describe("リストリテラル") {
    val expectations = List[(String, Value)](
      "リスト()" -> ObjectValue(listOf[Any]()),
      "リスト(1)" -> ObjectValue(listOf(BigInt(1))),
      """リスト("a")""" -> ObjectValue(listOf("a")),
      "リスト(1, 2)" -> ObjectValue(listOf(BigInt(1), BigInt(2))),
      """|リスト(
         | 1
         | 2
         |)
      """.stripMargin -> ObjectValue(listOf(BigInt(1), BigInt(2))),
      """|リスト(1,
         |
         | 2)
      """.stripMargin -> ObjectValue(listOf(BigInt(1), BigInt(2))),
      """|リスト(1
         |
         | 2)
      """.stripMargin -> ObjectValue(listOf(BigInt(1), BigInt(2))),
      """|リスト(1 +
         |
         | 2)
      """.stripMargin -> ObjectValue(listOf(BigInt(3))),
      """|リスト(1, 2
         | 3)
      """.stripMargin -> ObjectValue(listOf(BigInt(1), BigInt(2), BigInt(3))),
      """|リスト(
         | 1 2
         | 3 4
         |)
      """.stripMargin -> ObjectValue(listOf(BigInt(1), BigInt(2), BigInt(3), BigInt(4))),
      """|リスト(
         |  リスト(1 2)
         |  リスト(3 4)
         |)
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
}