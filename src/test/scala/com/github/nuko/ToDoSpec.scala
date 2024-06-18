package com.github.nuko

class ToDoSpec extends SpecHelper {
  describe("「後で埋める」 ブロック") {
    it("評価されたら例外が投げられる") {
      intercept[RuntimeException] {
        E(
          """
            |ブロック fact(n) は もし (n < 2) 後で埋める() でなければ n * fact(n - 1)
            |fact(0)
          """.stripMargin
        )
      }
    }
  }
}
