package com.github.nuko

class DictionarySpec extends SpecHelper {
  describe("キーを含む") {
    val expectations: List[(String, Value)] = List(
      """
        |辞書["名前" -> "Kota Mizushima" "年齢" -> "33"] 「辞書」#containsKey "名前"
      """.stripMargin -> BoxedBoolean(true),
      """
        |辞書["名前" -> "Kota Mizushima" "年齢" -> "33"] 「辞書」#containsKey "年齢"
      """.stripMargin -> BoxedBoolean(true),
      """
        |辞書["名前" -> "Kota Mizushima" "年齢" -> "33"] 「辞書」#containsKey "hoge"
      """.stripMargin -> BoxedBoolean(false)
    )

    expectations.foreach{ case (in, expected) =>
      it(s"${in} evaluates to ${expected}") {
        assert(expected == E(in))
      }
    }
  }

  describe("含む") {
    val expectations: List[(String, Value)] = List(
      """
        |辞書["名前" -> "Kota Mizushima" "年齢" -> "33"] 「辞書」#containsValue "33"
      """.stripMargin -> BoxedBoolean(true),
      """
        |辞書["名前" -> "Kota Mizushima" "年齢" -> "33"] 「辞書」#containsValue "Kota Mizushima"
      """.stripMargin -> BoxedBoolean(true),
      """
        |辞書["名前" -> "Kota Mizushima" "年齢" -> "33"] 「辞書」#containsValue "hoge"
      """.stripMargin -> BoxedBoolean(false)
    )
    expectations.zipWithIndex.foreach{ case ((in, expected), i) =>
      it(s"${in} evaluates to ${expected}") {
        assert(expected == E(in))
      }
    }
  }

  describe("取得") {
    val expectations: List[(String, Value)] = List(
      """
        |辞書["名前" -> "水島宏太" "年齢" -> "40"] 「辞書」#get "年齢"
      """.stripMargin -> ObjectValue("40"),
      """
        |辞書["名前" -> "水島宏太" "年齢" -> "40"] 「辞書」#get "名前"
      """.stripMargin -> ObjectValue("水島宏太"),
      """
        |辞書["名前" -> "水島宏太" "年齢" -> "33"] 「辞書」#get "性別"
      """.stripMargin -> ObjectValue(null)
    )
    expectations.zipWithIndex.foreach{ case ((in, expected), i) =>
      it(s"${in} evaluates to ${expected}") {
        assert(expected == E(in))
      }
    }
  }

  describe("isEmpty") {
    expect("empty map should be isEmpty")(
      """
        |「辞書」#isEmpty(辞書[])
      """.stripMargin -> BoxedBoolean(true)
    )
    expect("non empty map should not be isEmpty")(
      """
        |「辞書」#isEmpty(辞書["x" -> 1 "y" -> 2])
      """.stripMargin -> BoxedBoolean(false)
    )
  }
}
