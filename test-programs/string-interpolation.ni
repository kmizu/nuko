変数 x は 10
変数 y は 20
assertResult("x = 10, y = 20")("x = #{x :> *}, y = #{y :> *}")
assertResult("x + y = 30")("x + y = #{(x + y) :> *}")