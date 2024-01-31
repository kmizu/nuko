package com.github.nuko

class ToDoSpec extends SpecHelper {
  describe("ToDo() ブロック") {
    it("throw RuntimeException when it is evaluated") {
      intercept[RuntimeException] {
        E(
          """
            |ブロック fact(n) は もし (n < 2) ToDo() でなければ n * fact(n - 1)
            |fact(0)
          """.stripMargin
        )
      }
    }
  }
}
