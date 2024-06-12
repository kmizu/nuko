// 文字列関数
変数 pattern は "[0-9]+"
assertResult("F")(substring("FOO", 0, 1))
assertResult("R")(at("BAR", 2))
assertResult(true)(matches("199", pattern))
assertResult(true)(matches("200", pattern))
assertResult(false)(matches("a", pattern))
変数 sub は substring
assertResult("B")(sub("BAR", 0, 1))

// 数値関数
assertResult(1.4142135623730951)(sqrt(2.0))
assertResult(3.0)(sqrt(9.0))
assertResult(3)(int(3.14159265359))
assertResult(10.0)(double(10))
assertResult(1)(floor(1.5))
assertResult(-1)(floor(-1.5))
assertResult(5)(ceil(4.4))
assertResult(5)(ceil(4.5))
assertResult(-4)(ceil(-4.4))
assertResult(-4)(ceil(-4.5))
assertResult(10.5)(abs(10.5))
assertResult(10.5)(abs(-10.5))

// 確認関数
assert(2 == 1 + 1)
// assert(3 > 5) // => throws AssertionError

変数 add は (x, y) => {
  x + y
}
変数 time は stopwatch( => {
  sleep(1000)
  println("1")
})
println("it took #{time} milli seconds")
printlnError("this param is displayed into standard error")
assertResult(5)(add(2, 3))
変数 list は new java.util.ArrayList
list->add(4)
list->add(1)
list->add(2)
list->add(3)
assertResult(リスト(4 1 2 3))((list :> List<整数>))
assertResult(5.0)(sqrt(3.0 * 3.0 + 4.0 * 4.0))
assertResult(6)(int(6.5))
assertResult(7)(floor(7.5))
