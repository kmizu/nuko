package com.github.nuko


/**
  * Created by Mizushima on 2016/05/30.
  */
class PlaceholderSpec extends TestSuiteHelper {
  test("binary expression has  one placeholder") {
    val result = E(
      """
        |変数 xs は リスト(1 2 3)
        |map(xs)(_ + 1)
      """.stripMargin
    )
    assertResult(result)(ObjectValue(listOf(BigInt(2), BigInt(3), BigInt(4))))
  }

  test("binary expression has two placeholder (1)") {
    val result = E(
      """
        |変数 add は _ + _
        |foldLeft(リスト(1 2 3))(0)(add)
      """.stripMargin
    )
    assertResult(result)(BoxedInt(6))
  }

  test("binary expression has two placeholder (2)") {
    val result = E(
      """
        |foldLeft(リスト(1 2 3))(0)(_ + _)
      """.stripMargin
    )
    assertResult(result)(BoxedInt(6))
  }

  test("unary expression - has one placeholder") {
    val result = E(
      """
        |map(リスト(1 2 3))(- _)
      """.stripMargin
    )
    assertResult(result)(ObjectValue(listOf(BigInt(-1), BigInt(-2), BigInt(-3))))
  }
  test("unary expression + has one placeholder") {
    val result = E(
      """
        |map(リスト(1 2 3))(+ _)
      """.stripMargin
    )
    assertResult(result)(ObjectValue(listOf(BigInt(1), BigInt(2), BigInt(3))))
  }
  test("variable declaration has one placeholder") {
    val result = E(
      """
        |変数 id は _
        |map(リスト(1))(id)
      """.stripMargin
    )
    assertResult(result)(ObjectValue(listOf(BigInt(1))))
  }
  test("function declaration has one placeholder") {
    val result = E(
      """
        |ブロック f(x) は _
        |map(リスト(1))(f(1))
      """.stripMargin
    )
    assertResult(result)(ObjectValue(listOf(BigInt(1))))
  }
}
