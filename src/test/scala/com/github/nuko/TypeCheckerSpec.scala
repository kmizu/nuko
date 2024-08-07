package com.github.nuko

import com.github.nuko.Value.*

/**
  * Created by Mizushima on 2016/05/30.
  */
class TypeCheckerSpec extends SpecHelper {
  describe("assignment") {
    val expectations: List[(String, Value)] = List(
      """
        |変数aは1
        |a
      """.stripMargin -> BoxedInt(1),
      """
        |変数aは1
        |a ← a + 1
        |a
      """.stripMargin -> BoxedInt(2),
      """
        |変数sは"FOO"
        |s ← s+s
        |s
      """.stripMargin -> ObjectValue("FOOFOO")
    )

    expectations.zipWithIndex.foreach { case ((in, expected), i) =>
      it(s"${in} evaluates to ${expected}") {
        assert(expected == E(in))
      }
    }
  }

  describe("関数の型検査は成功する") {
    val expectations: List[(String, Value)] = List(
      """
        |関数 「加算」(x の種類は 整数, y の種類は 整数): 整数 は x + y
        |変数 f: (整数, 整数) => 整数 は 「加算」
        |f(2, 3)
      """.stripMargin -> BoxedInt(5))

    expectations.zipWithIndex.foreach { case ((in, expected), i) =>
      it(s"${in} evaluates to ${expected}") {
        assert(expected == E(in))
      }
    }
  }

  describe("function type doesn't match ") {
    val illTypedPrograms: List[String] = List(
      """
        |関数 f(x, y) は x + y
        |f(10)
      """.stripMargin
    )
    illTypedPrograms.zipWithIndex.foreach { case (in, i) =>
      it(s"expectation  ${i}") {
        val e = intercept[TyperException] {
          E(in)
        }
      }
    }
  }

}

