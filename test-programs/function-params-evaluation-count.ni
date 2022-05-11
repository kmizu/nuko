変数 count は 0

変数 add は (x, y) => {
  count = count + 1
  x + y
}

変数 num1 は add(1, 2)
assertResult(1)(count)

変数 num2 は add(1, 2)
assertResult(2)(count)
