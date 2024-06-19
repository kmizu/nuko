package com.github.nuko


/**
  * Created by Mizushima on 2016/05/30.
  */
class PlaceholderSpec extends TestSuiteHelper {
  test("binary expression has  one placeholder") {
    val result = E(
      """
        |変数 xs は リスト(1 2 3)
        |変換(xs)(_ + 1)
      """.stripMargin
    )
    assertResult(result)(ObjectValue(listOf(BigInt(2), BigInt(3), BigInt(4))))
  }

  test("binary expression has two placeholder (1)") {
    val result = E(
      """
        |変数 add は _ + _
        |たたむ(リスト(1 2 3))(0)(add)
      """.stripMargin
    )
    assertResult(result)(BoxedInt(6))
  }

  test("binary expression has two placeholder (2)") {
    val result = E(
      """
        |たたむ(リスト(1 2 3))(0)(_ + _)
      """.stripMargin
    )
    assertResult(result)(BoxedInt(6))
  }

  test("unary expression - has one placeholder") {
    val result = E(
      """
        |変換(リスト(1 2 3))(- _)
      """.stripMargin
    )
    assertResult(result)(ObjectValue(listOf(BigInt(-1), BigInt(-2), BigInt(-3))))
  }
  test("unary expression + has one placeholder") {
    val result = E(
      """
        |変換(リスト(1 2 3))(+ _)
      """.stripMargin
    )
    assertResult(result)(ObjectValue(listOf(BigInt(1), BigInt(2), BigInt(3))))
  }
  test("variable declaration has one placeholder") {
    val result = E(
      """
        |変数 id は _
        |変換(リスト(1))(id)
      """.stripMargin
    )
    assertResult(result)(ObjectValue(listOf(BigInt(1))))
  }
  test("function declaration has one placeholder") {
    val result = E(
      """
        |関数 f(x) は _
        |変換(リスト(1))(f(1))
      """.stripMargin
    )
    assertResult(result)(ObjectValue(listOf(BigInt(1))))
  }
}
