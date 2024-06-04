package com.github.nuko

class ListSpec extends SpecHelper {
  describe("head") {
    val expectations: List[(String, Value)] = List(
      """
        |head(リスト(1))
      """.stripMargin -> BoxedInt(1),
      """
        |head(リスト(2 1))
      """.stripMargin -> BoxedInt(2),
      """
        |head(リスト(3 2 1))
      """.stripMargin -> BoxedInt(3)
    )

    expectations.foreach{ case (in, expected) =>
      it(s"${in} evaluates to ${expected}") {
        assert(expected == E(in))
      }
    }
  }

  describe("tail") {
    val expectations: List[(String, Value)] = List(
      """
        |tail(リスト(1))
      """.stripMargin -> ObjectValue(listOf()),
      """
        |tail(リスト(2 1))
      """.stripMargin -> ObjectValue(listOf(BigInt(1))),
      s"""
        |tail(リスト(3 2 1))
      """.stripMargin -> ObjectValue(listOf(BigInt(2), BigInt(1)))
    )
    expectations.zipWithIndex.foreach{ case ((in, expected), i) =>
      it(s"${in} evaluates to ${expected}") {
        assert(expected == E(in))
      }
    }
  }

  describe("cons") {
    val expectations: List[(String, Value)] = List(
      """
         | cons(1)(リスト())
      """.stripMargin -> ObjectValue(listOf(BigInt(1))),
      """
         | cons(2)(リスト(1))
      """.stripMargin -> ObjectValue(listOf(BigInt(2), BigInt(1))),
      """
        | cons(3)(リスト(2, 1))
      """.stripMargin -> ObjectValue(listOf(BigInt(3), BigInt(2), BigInt(1))),

      """
        | 3 #cons (2 #cons (1 #cons リスト()))
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

  describe("map") {
    expect("for empty list")(
      """
        |map(リスト())((x) => x + 1)
      """.stripMargin, ObjectValue(listOf())
    )
    expect("for a non empty list and a function that add arg to 1")(
      """
        |map(リスト(1 2 3))((x) => x + 1)
      """.stripMargin, ObjectValue(listOf(2, 3, 4))
    )
  }
}