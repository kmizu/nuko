// OK: whole one line comment
assertResult(true)(true)

変数 i は 1 + // line comment in the middle of a expression
            2 // => 3 : line comment at the end of expression

assertResult(3)(i)
