ブロック myFoldLeft(list) は (z) => (f) => {
  もし (isEmpty(list)) z でなければ myFoldLeft(tail(list))(f(z, head(list)))(f)
}
assertResult(10)(myFoldLeft(リスト(1 2 3 4))(0)((x, y) => x + y))
assertResult("ABC")(myFoldLeft(リスト("A" "B" "C"))("")((x, y) => x + y))
assertResult(リスト(4 3 2 1))(myFoldLeft(リスト(1 2 3 4))(リスト())((x, y) => y #cons x))
assertResult(リスト(4 3 2 1))(myFoldLeft(リスト(1 2 3 4))(リスト()){x, y => y #cons x})

変数 sum は myFoldLeft(リスト(1 2 3 4 5))(0){x, y => x + y}
assertResult(15)(sum)
assertResult(10.0)(foldLeft(リスト(1.0 2.0 3.0 4.0))(0.0){x, y => x + y})
