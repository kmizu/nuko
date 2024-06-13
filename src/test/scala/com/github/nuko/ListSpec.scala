package com.github.nuko

class ListSpec extends SpecHelper {
  describe("先頭") {
    val expectations: List[(String, Value)] = List(
      """
        |先頭(リスト(1))
      """.stripMargin -> BoxedInt(1),
      """
        |先頭(リスト(2 1))
      """.stripMargin -> BoxedInt(2),
      """
        |先頭(リスト(3 2 1))
      """.stripMargin -> BoxedInt(3)
    )

    expectations.foreach{ case (in, expected) =>
      it(s"${in} evaluates to ${expected}") {
        assert(expected == E(in))
      }
    }
  }

  describe("末尾") {
    val expectations: List[(String, Value)] = List(
      """
        |末尾(リスト(1))
      """.stripMargin -> ObjectValue(listOf()),
      """
        |末尾(リスト(2 1))
      """.stripMargin -> ObjectValue(listOf(BigInt(1))),
      s"""
        |末尾(リスト(3 2 1))
      """.stripMargin -> ObjectValue(listOf(BigInt(2), BigInt(1)))
    )
    expectations.zipWithIndex.foreach{ case ((in, expected), i) =>
      it(s"${in} evaluates to ${expected}") {
        assert(expected == E(in))
      }
    }
  }

  describe("構築") {
    val expectations: List[(String, Value)] = List(
      """
        |構築(1)(リスト())
      """.stripMargin -> ObjectValue(listOf(BigInt(1))),
      """
        |構築(2)(リスト(1))
      """.stripMargin -> ObjectValue(listOf(BigInt(2), BigInt(1))),
      """
        |構築(3)(リスト(2, 1))
      """.stripMargin -> ObjectValue(listOf(BigInt(3), BigInt(2), BigInt(1))),

      """
        |構築(3)(構築(2)(構築(1)(リスト())))
      """.stripMargin -> ObjectValue(listOf(BigInt(3), BigInt(2), BigInt(1)))
    )

    expectations.zipWithIndex.foreach { case ((in, expected), i) =>
      it(s"${in} evaluates to ${expected}") {
        assert(expected == E(in))
      }
    }
  }

  describe("size") {
    val expectations: List[(String, Value)] = List(
      """
        |size(リスト())
      """.stripMargin -> BoxedInt(0),
      """
        |size(リスト(1))
      """.stripMargin -> BoxedInt(1),
      """
        |size(リスト(2 1))
      """.stripMargin -> BoxedInt(2),
      """
        |size(リスト(3 2 1))
      """.stripMargin -> BoxedInt(3)
    )
    expectations.zipWithIndex.foreach{ case ((in, expected), i) =>
      it(s"${in} evaluates to ${expected}") {
        assert(expected == E(in))
      }
    }
  }

  describe("isEmpty") {
    val expectations: List[(String, Value)] = List(
      """
        |isEmpty(リスト())
      """.stripMargin -> BoxedBoolean(true),
      """
        |isEmpty(リスト(1))
      """.stripMargin -> BoxedBoolean(false),
      """
        |isEmpty(リスト(2 1))
      """.stripMargin -> BoxedBoolean(false),
      """
        |isEmpty(リスト(3 2 1))
      """.stripMargin -> BoxedBoolean(false)
    )
    expectations.zipWithIndex.foreach{ case ((in, expected), i) =>
      it(s"${in} evaluates to ${expected}") {
        assert(expected == E(in))
      }
    }
  }

  describe("変換") {
    expect("空リストに対して")(
      """
        |変換(リスト())((x) => x + 1)
      """.stripMargin, ObjectValue(listOf())
    )
    expect("for a non empty list and a function that add arg to 1")(
      """
        |変換(リスト(1 2 3))((x) => x + 1)
      """.stripMargin, ObjectValue(listOf(2, 3, 4))
    )
  }
}