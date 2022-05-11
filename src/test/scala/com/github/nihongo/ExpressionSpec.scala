package com.github.nihongo

/**
  * Created by Mizushima on 2016/05/30.
  */
class ExpressionSpec extends SpecHelper {
  describe("代入式") {
    it("が正しく評価されていること") {
      assertResult(
        E(
          """
            |変数aは1
            |a
          """.stripMargin))(BoxedInt(1))
      assertResult(
        E(
          """
            |変数aは1
            |a = a 足す 1
            |a
          """.stripMargin))(BoxedInt(2))
      assertResult(
        E(
          """
            |変数aは1
            |a += 1
            |a
          """.stripMargin))(BoxedInt(2))
      assertResult(
        E(
          """
            |変数aは1
            |a -= 1
            |a
          """.stripMargin))(BoxedInt(0))
      assertResult(
        E(
          """
            |変数aは3
            |a *= 2
            |a
          """.stripMargin))(BoxedInt(6))
      assertResult(
        E(
          """
            |変数aは5
            |a /= 2
            |a
          """.stripMargin))(BoxedInt(2))
    }
  }

  describe("繰り返し式") {
    it("が正しく評価されていること") {
      assertResult(
        E(
          """
            |変数iは1
            |i < 10 のあいだ {
            |  iを1増やす
            |} をくりかえす
            |i
          """.stripMargin))(BoxedInt(10))
      assertResult(
        E(
          """
            |変数iは10
            |i >= 0 のあいだ {
            |  iを1減らす
            |} をくりかえす
            |i
          """.stripMargin))(BoxedInt(-1))
      assertResult(
        E(
          s"""
             |変数bufはnew java.lang.StringBuffer
             |変数iは0
             |i <= 5 のあいだ {
             |  buf->append("#{i}")
             |  i = i 足す 1
             |} をくりかえす
             |buf->toString()
      """.stripMargin))(ObjectValue("012345"))
    }
  }

  describe("anonymous function") {
    it("is evaluated correctly") {
      assertResult(
        E("""
            |変数addは(x, y) => x 足す y
            |add(3, 3)
          """.stripMargin))(BoxedInt(6))
    }
  }

  describe("論理式") {
    it("が正しく評価されていること"){
      assertResult(
        E(
          """
            |変数iは1
            |0 <= i かつ i <= 10
          """.stripMargin))(BoxedBoolean(true))
      assertResult(
        E(
          """
            |変数iは-1
            |0 <= i かつ i <= 10
          """.stripMargin))(BoxedBoolean(false))
      var input =
        """
            |変数iは-1
            |i < 0 または i > 10
        """.stripMargin
      assertResult(
        E(input)
      )(
        BoxedBoolean(true)
      )
      input =
        """
          |変数iは1
          |i < 0 または i > 10
        """.stripMargin
      assertResult(
        E(input)
      )(BoxedBoolean(false))
    }

    describe("foreach expression") {
      it("が正しく評価されていること") {
        assertResult(
          E(
            """
              |変数newListはnew java.util.ArrayList
              |foreach(a in [1, 2, 3, 4, 5]) {
              |  newList->add((a :> Int) * 2)
              |}
              |newList
            """.stripMargin))(ObjectValue(listOf(2, 4, 6, 8, 10)))
      }
    }

    describe("「もし」 式") {
      it("が正しく評価されていること") {
        assertResult(
          E(
            """
              |もし (真) 1.0 でなければ 2.0
            """.stripMargin))(BoxedDouble(1.0))
        assertResult(
          E(
            """
              |もし (偽) 1.0 でなければ 2.0
            """.stripMargin))(BoxedDouble(2.0))
      }
    }

    describe("三項式") {
      it("が正しく評価されていること") {
        assertResult(
          E(
            """
              |変数xは1
              |x < 2 ならば "A" でなければ "B"
            """.stripMargin))(ObjectValue("A"))
        assertResult(
          E(
            """
              |変数xは2
              |x < 2 ならば "A" でなければ "B"
            """.stripMargin))(ObjectValue("B"))
      }
    }

    describe("関数定義") {
      it("が正しく評価されていること") {
        assertResult(
          E(
            """
              |関数 add(x, y) は x + y
              |add(2, 3)
            """.stripMargin))(BoxedInt(5))
        assertResult(
          E(
            """
              |関数 fact(n) は もし(n < 2) 1 でなければ (n * fact(n - 1))
              |fact(4)
            """.stripMargin))(BoxedInt(24))
        assertResult(
          E(
            """
              |関数 none() は 24 後始末 "none"
              |none()
            """.stripMargin))(BoxedInt(24))
        assertResult(
          E(
            """
              |関数 hello() は {
              |  "Hello"
              |  0
              |} 後始末 {
              |  "World"
              |}
              |hello()
            """.stripMargin))(BoxedInt(0))
      }
    }
  }
}
