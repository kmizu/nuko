変数 dict1 は 辞書["A" -> 1, "B" -> 2]
assertResult(1)(dict1 Map#get "A")
assertResult(2)(dict1 Map#get "B")
assertResult(null)(dict1 Map#get "C")

変数 dict2は 辞書[
    "A" -> 1
    "B" -> 2
]
assertResult(dict1)(dict2)