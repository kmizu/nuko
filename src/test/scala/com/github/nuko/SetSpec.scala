package com.github.nuko
import com.github.nuko.Value.fromKlassic

import java.util.{Set => JSet}

class SetSpec extends SpecHelper {
  describe("集合に要素を追加できる") {
    it("空集合に1を追加") {
      assert(ObjectValue(setOf(1)) == E("集合() 集合#追加 1"))
    }
    it("1要素の集合に2を追加") {
      assert(ObjectValue(setOf(1, 2)) == E("集合(1) 集合#追加 2"))
    }
    it("2要素の集合に3を追加") {
      assert(ObjectValue(setOf(1, 2, 3)) == E("集合(1 2) 集合#追加 3"))
    }
  }

  describe("集合から要素を削除できる") {
    it("空集合から1を削除") {
      assert(ObjectValue(setOf()) == E("集合() 集合#削除 1"))
    }
    it("1要素の集合から要素を削除") {
      assert(ObjectValue(setOf()) == E("集合(1) 集合#削除 1"))
    }
    it("2要素の集合から1を削除") {
      assert(ObjectValue(setOf(2)) == E("集合(1 2) 集合#削除 1"))
    }
  }

  describe("集合がある要素を含むことを判定できる") {
    it("空集合") {
      assert(BoxedBoolean(false) == E("集合() 集合#要素を含む 1"))
    }
    it("1要素の集合") {
      assert(BoxedBoolean(true) == E("集合(1) 集合#要素を含む 1"))
    }
    it("2要素の集合") {
      assert(BoxedBoolean(true) == E("集合(1 2) 集合#要素を含む 1"))
    }
  }

  describe("集合のサイズを取得できる") {
    it("空集合") {
      assert(BoxedInt(0) == E("集合#サイズ(集合())"))
    }
    it("1要素の集合") {
      assert(BoxedInt(1) == E("集合#サイズ(集合(1))"))
    }
    it("2要素の集合") {
      assert(BoxedInt(2) == E("集合#サイズ(集合(1 2))"))
    }
  }

  describe("集合が空であることを判定できる") {
    it("空集合") {
      assert(BoxedBoolean(true) == E("集合#空である(集合())"))
    }
    it("1要素の集合") {
      assert(BoxedBoolean(false) == E("集合#空である(集合(1))"))
    }
    it("2要素の集合") {
      assert(BoxedBoolean(false) == E("集合#空である(集合(1 2))"))
    }
  }

  describe("構築") {
    val expectations: List[(String, Value)] = List(
      """
        |1 #構築 リスト()
      """.stripMargin -> ObjectValue(listOf(BigInt(1))),
      """
        |2 #構築 リスト(1)
      """.stripMargin -> ObjectValue(listOf(BigInt(2), BigInt(1))),
      """
        |3 #構築 リスト(2, 1)
      """.stripMargin -> ObjectValue(listOf(BigInt(3), BigInt(2), BigInt(1))),

      """
        |3 #構築 (2 #構築 (1 #構築 リスト()))
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