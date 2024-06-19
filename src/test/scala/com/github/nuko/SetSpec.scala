package com.github.nuko
import com.github.nuko.Value.fromKlassic

import java.util.{Set => JSet}

class SetSpec extends SpecHelper {
  describe("集合に要素を追加できる") {
    it("空集合に1を追加") {
      assert(ObjectValue(setOf(1)) == E("集合() 追加 1"))
    }
    it("1要素の集合に2を追加") {
      assert(ObjectValue(setOf(1, 2)) == E("集合(1) 追加 2"))
    }
    it("2要素の集合に3を追加") {
      assert(ObjectValue(setOf(1, 2, 3)) == E("集合(1 2) 追加 3"))
    }
  }

  describe("集合から要素を削除できる") {
    it("空集合から1を削除") {
      assert(ObjectValue(setOf()) == E("集合() 削除 1"))
    }
    it("1要素の集合から要素を削除") {
      assert(ObjectValue(setOf()) == E("集合(1) 削除 1"))
    }
    it("2要素の集合から1を削除") {
      assert(ObjectValue(setOf(2)) == E("集合(1 2) 削除 1"))
    }
  }

  describe("集合がある要素を含むことを判定できる") {
    it("空集合") {
      assert(BoxedBoolean(false) == E("集合() 要素を含む 1"))
    }
    it("1要素の集合") {
      assert(BoxedBoolean(true) == E("集合(1) 要素を含む 1"))
    }
    it("2要素の集合") {
      assert(BoxedBoolean(true) == E("集合(1 2) 要素を含む 1"))
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
}