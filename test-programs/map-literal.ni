変数 map1 は %["A": 1, "B": 2]
assertResult(1)(map1 Map#get "A") 
assertResult(2)(map1 Map#get "B")
assertResult(null)(map1 Map#get "C")

変数 map2 は %[
    "A" : 1
    "B" : 2
]
assertResult(map1)(map2)

変数 map3 は %["A": 1, "B": 2]
