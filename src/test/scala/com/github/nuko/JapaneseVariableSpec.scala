package com.github.nuko

/**
  * Created by Mizushima on 2022/08/12.
  */
class JapaneseVariableSpec extends SpecHelper {
  describe("漢字変数名") {
    it("変数「今年」が定義できること") {
      assertResult(
        E(
          """
            |変数「今年」は2022
            |「今年」
          """.stripMargin))(BoxedInt(2022))
    }
    it("変数「国」が定義できること") {
      assertResult(
        E(
          """
            |変数「国」は"日本"
            |「国」
          """.stripMargin))(ObjectValue("日本")
      )
    }
  }
  describe("ひらがな変数名") {
    it("変数「あなた」が定義できること") {
      assertResult(
        E(
          """
            |変数「あなた」は"You"
            |「あなた」
          """.stripMargin))(ObjectValue("You"))
    }
    it("変数「わたし」が定義できること") {
      assertResult(
        E(
          """
            |変数「わたし」は"I"
            |「わたし」
          """.stripMargin))(ObjectValue("I")
      )
    }
  }
  describe("カタカナ変数名") {
    it("変数「アメリカ」が定義できること") {
      assertResult(
        E(
          """
            |変数「アメリカ」は"America"
            |「アメリカ」
          """.stripMargin))(ObjectValue("America"))
    }
    it("変数「アルバイト」が定義できること") {
      assertResult(
        E(
          """
            |変数「アルバイト」は"part-time job"
            |「アルバイト」
          """.stripMargin))(ObjectValue("part-time job")
      )
    }
  }
  describe("漢字かな混じり変数名") {
    it("変数「日本の首都」が定義できること") {
      assertResult(
        E(
          """
            |変数「日本の首都」は"東京"
            |「日本の首都」
          """.stripMargin))(ObjectValue("東京"))
    }
    it("変数「職場までの距離」が定義できること") {
      assertResult(
        E(
          """
            |変数「職場までの距離」は400
            |「職場までの距離」
          """.stripMargin))(BoxedInt(400))
    }
  }
}