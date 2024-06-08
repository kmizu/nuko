package com.github.nuko

class RealSpec extends SpecHelper {
  describe("real literal") {
    val expectations = List[(String, Value)](
      "2.0" -> BoxedReal(2.0),
      "2.5" -> BoxedReal(2.5),
      "+0.0" -> BoxedReal(+0.0),
      "-0.0" -> BoxedReal(-0.0),
      "0.1" -> BoxedReal(+0.1),
      "-0.1" -> BoxedReal(-0.1)
    )

    expectations.zipWithIndex.foreach { case ((in, expected), i) =>
      it(s"${in} evaluates to ${expected}") {
        assert(expected == E(in))
      }
    }
  }

  describe("real operations") {
    assert(E("0.1") == E("1.0 - 0.9"))
  }
}