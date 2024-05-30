// map
assertResult(リスト[2 3 4])(map(リスト[1 2 3])((x) => x + 1))
assertResult(リスト[3 4 5])(map(リスト[2 3 4]){x => x + 1})

// head
assertResult(1)(head(リスト[1 2 3 4]))

// tail
assertResult(リスト[2 3 4])(tail(リスト[1 2 3 4]))

// cons
assertResult(リスト[1 2 3 4])(cons(1)(リスト[2 3 4]))

// size
assertResult(5)(size(リスト[1 2 3 4 5]))

// isEmpty
assertResult(true)(isEmpty(リスト[]))
assertResult(false)(isEmpty(リスト[1 2 3]))

// foldLeft
assertResult(10)(foldLeft(リスト[1 2 3 4])(0)((x, y) => x + y))
assertResult(10.0)(foldLeft(リスト[1.0 2.0 3.0 4.0])(0.0){x, y => x + y})
assertResult(24.0)(foldLeft(リスト[1.0 2.0 3.0 4.0])(1.0){x, y => x * y})
