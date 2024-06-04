変数 list は new java.util.ArrayList
list->add(1)
list->add(2)
assertResult(リスト(1, 2))(list :> List<整数>)
変数 buffer は new java.lang.StringBuffer
buffer->append("A")->append("B")->append("C")
assertResult("ABC")(buffer->toString)
変数 a は リスト("FOO", "BAR", "BAZ")
変数 b は リスト(
  "FOO"
  "BAR"
  "BAZ"
)
assertResult(a)(b)
