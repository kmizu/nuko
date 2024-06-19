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
        |1 構築 リスト()
      """.stripMargin -> ObjectValue(listOf(BigInt(1))),
      """
        |2 構築 リスト(1)
      """.stripMargin -> ObjectValue(listOf(BigInt(2), BigInt(1))),
      """
        |3 構築 リスト(2, 1)
      """.stripMargin -> ObjectValue(listOf(BigInt(3), BigInt(2), BigInt(1))),

      """
        |3 構築 (2 構築 (1 構築 リスト()))
      """.stripMargin -> ObjectValue(listOf(BigInt(3), BigInt(2), BigInt(1)))
    )

    expectations.zipWithIndex.foreach { case ((in, expected), i) =>
      it(s"${in} evaluates to ${expected}") {
        assert(expected == E(in))
      }
    }
  }

  describe("サイズを取得できる") {
    it("空リスト") {
      assert(BoxedInt(0) == E("サイズ(リスト())"))
    }
    it("要素が1つのリスト") {
      assert(BoxedInt(1) == E("サイズ(リスト(1))"))
    }
    it("要素が2つのリスト") {
      assert(BoxedInt(2) == E("サイズ(リスト(2 1))"))
    }
    it("要素が3つのリスト") {
      assert(BoxedInt(3) == E("サイズ(リスト(3 2 1))"))
    }
  }

  describe("リストが空であることを判定できる") {
    it("空リスト") {
      assert(BoxedBoolean(true) == E("空である(リスト())"))
    }
    it("要素が1つのリスト") {
      assert(BoxedBoolean(false) == E("空である(リスト(1))"))
    }
    it("要素が2つのリスト") {
      assert(BoxedBoolean(false) == E("空である(リスト(2 1))"))
    }
    it("要素が3つのリスト") {
      assert(BoxedBoolean(false) == E("空である(リスト(3 2 1))"))
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