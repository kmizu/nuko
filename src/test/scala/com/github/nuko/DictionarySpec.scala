package com.github.nuko

class DictionarySpec extends SpecHelper {
  describe("辞書がキーを含むことを判定できる") {
    it("キー「名前」") {
      assert(BoxedBoolean(true) == E("辞書[\"名前\" → \"Kota Mizushima\" \"年齢\" → \"33\"] 辞書#キーを含む \"名前\""))
    }
    it("キー「年齢」") {
      assert(BoxedBoolean(true) == E("辞書[\"名前\" → \"Kota Mizushima\" \"年齢\" → \"33\"] 辞書#キーを含む \"年齢\""))
    }
    it("キー「hoge」") {
      assert(BoxedBoolean(false) == E("辞書[\"名前\" → \"Kota Mizushima\" \"年齢\" → \"33\"]  辞書#キーを含む　\"hoge\""))
    }
  }

  describe("辞書が値を含むことを判定できる") {
    it("値 = 33") {
      assert(BoxedBoolean(true) == E("辞書[\"名前\" -> \"Kota Mizushima\" \"年齢\" -> \"33\"] 辞書#値を含む \"33\""))
    }
    it("値 = Kota Mizushima") {
      assert(BoxedBoolean(true) == E("辞書[\"名前\" -> \"Kota Mizushima\" \"年齢\" -> \"33\"] 辞書#値を含む \"Kota Mizushima\""))
    }
    it("値 = hoge") {
      assert(BoxedBoolean(false) == E("辞書[\"名前\" -> \"Kota Mizushima\" \"年齢\" -> \"33\"]  辞書#値を含む　\"hoge\""))
    }
  }

  describe("辞書からキーを指定して値を取得できる") {
    it("キー = 年齢") {
      assert(ObjectValue("33") == E("辞書[\"名前\" -> \"Kota Mizushima\" \"年齢\" -> \"33\"] 辞書#値を取得 \"年齢\""))
    }
    it("キー = 名前") {
      assert(ObjectValue("Kota Mizushima") == E("辞書[\"名前\" -> \"Kota Mizushima\" \"年齢\" -> \"33\"] 辞書#値を取得 \"名前\""))
    }
    it("キー = 性別") {
      assert(ObjectValue(null) == E("辞書[\"名前\" -> \"Kota Mizushima\" \"年齢\" -> \"33\"]  辞書#値を取得　\"性別\""))
    }
  }

  describe("辞書が空であることを判定できる") {
    it("空の辞書")(
      assert(
        BoxedBoolean(true) == E("辞書#空である(辞書[])")
      )
    )
    it("空でない辞書")(
      assert(
        BoxedBoolean(false) == E("辞書#空である(辞書[\"x\" -> 1 \"y\" -> 2])")
      )
    )
  }
}
