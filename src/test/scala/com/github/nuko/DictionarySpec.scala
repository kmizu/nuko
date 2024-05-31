package com.github.nuko

class DictionarySpec extends SpecHelper {
  describe("containsKey") {
    val expectations: List[(String, Value)] = List(
      """
        |辞書["名前" -> "Kota Mizushima" "年齢" -> "33"] Map#containsKey "名前"
      """.stripMargin -> BoxedBoolean(true),
      """
        |辞書["名前" -> "Kota Mizushima" "年齢" -> "33"] Map#containsKey "年齢"
      """.stripMargin -> BoxedBoolean(true),
      """
        |辞書["名前" -> "Kota Mizushima" "年齢" -> "33"] Map#containsKey "hoge"
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
        |辞書["名前" -> "Kota Mizushima" "年齢" -> "33"] Map#containsValue "33"
      """.stripMargin -> BoxedBoolean(true),
      """
        |辞書["名前" -> "Kota Mizushima" "年齢" -> "33"] Map#containsValue "Kota Mizushima"
      """.stripMargin -> BoxedBoolean(true),
      """
        |辞書["名前" -> "Kota Mizushima" "年齢" -> "33"] Map#containsValue "hoge"
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
        |辞書["名前" -> "水島宏太" "年齢" -> "40"] Map#get "年齢"
      """.stripMargin -> ObjectValue("40"),
      """
        |辞書["名前" -> "水島宏太" "年齢" -> "40"] Map#get "名前"
      """.stripMargin -> ObjectValue("水島宏太"),
      """
        |辞書["名前" -> "水島宏太" "年齢" -> "33"] Map#get "性別"
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
        |Map#isEmpty(辞書[])
      """.stripMargin -> BoxedBoolean(true)
    )
    expect("non empty map should not be isEmpty")(
      """
        |Map#isEmpty(辞書["x" -> 1 "y" -> 2])
      """.stripMargin -> BoxedBoolean(false)
    )
  }
}
