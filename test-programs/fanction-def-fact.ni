関数 fact(n) は もし (n < 2) 1 でなければ n * fact(n - 1)

assertResult(1)(fact(0))
assertResult(1)(fact(1))
assertResult(2)(fact(2))
assertResult(6)(fact(3))
assertResult(24)(fact(4))
assertResult(120)(fact(5))

// The result of type inference of fact is : Int => Int
