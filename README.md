# 日本語プログラミング言語「ぬこ」 [![Build Status](https://github.com/kmizu/nuko/actions/workflows/scala.yml/badge.svg?branch=main)](https://github.com/kmizu/nuko/actions)

## りーどみー

「ぬこ」はできるだけ日本語のように書くことができ、初心者にとって読みやすく理解しやすいことを目指したログラミング言語です。

変数を使った簡単なプログラムは以下のように記述することができます：

```
変数 挨拶 は　(時間 < 12) ならば "おはようございます" でなければ "こんにちは"
```

最近、プログラミング教育の必要性が叫ばれています。しかし、プログラミング言語は英語で書かれていることが多いため、英語が苦手な人にとってはハードルが高いと感じることがあります。ぬこは、日本語でプログラミングを学ぶ人々にとって、プログラミング言語の学習をより身近なものにすることを目指しています。

以下はぬこの特徴です：

* 型があるけどあまり意識しないでよい仕様
* 自然な日本語表現としてプログラムを書ける
  * ループを `{条件式} のあいだ {本体} くりかえす` のように書けます
  * 条件分岐を `{条件式} ならば　{本体} でなければ {本体}` のように書けます
  * `変数xはv` のように変数宣言ができます
* 各種データ構造が簡単に使えます
  * リスト: `リスト[1, 2, 3, 4, 5]`
  * 辞書: `辞書["apple" -> "りんご", "blue" -> "青"]`
  * 集合: `集合(1, 2, 3, 4, 5)`
* 関数型プログラミング言語としての機能を持っています
  * ですが、ぬこでプログラムを書くときに意識する必要はありません
  * 意識しないでも自然と関数型プログラミングを学べます

ぬこを使えば、日本語でお手軽に関数型プログラミングをできます。

## インストール

ぬこのインストールにはJava 17以降が必要です。

実行可能jarを[リリースページ（準備中）](https://github.com/kmizu/nuko/releases/tag/releases%2Fv0.0.1)からダウンロード可能です。適当なディレクトリに
ダウンロードしたnuko.jarを配置して、`java -jar nuko.jar`を実行してください。

## 使い方：

```
$ java -jar nuko.jar

Usage: java -jar nuko.jar (-f <fileName> | -e <expression>)
<fileName>   : read a program from <fileName> and execute it
-e <expression> : evaluate <expression>
```

次のようなプログラムを`hello.ni`という名前で保存してください：

```
println("Hello, World!")
```

このプログラムを実行するには、`java -jar nuko.jar hello.ni`とします：

```console
$ java -jar nuko.jar hello.ni

Hello, World!
```

## 構文

### 変数宣言

```
変数 one は 1 
```

変数`one`を宣言し、その初期値を`1`とします。

### 無名ブロック

```
変数 add は (x, y) => x + y
```

変数`add`を宣言し、その初期値を`(x, y) => x + y`とします。無名ブロックは
`(引数) => 式`という形で書きます。無名ブロックは、ブロックオブジェクトを生成します。

無名ブロックの本体が複数の式に渡る場合、以下のように書けます：

```
変数 printAndAdd は (x, y) => {
  println(x)
  println(y)
  x + y
}
```

### ブロック定義

名前のついたブロックを定義したいこともあるでしょう。その場合は、以下のように書きます：

```
ブロック fact(n) は もし(n < 2) 1 でなければ n * fact(n - 1)
fact(0) // 1
fact(1) // 1
fact(2) // 2
fact(3) // 6
fact(4) // 24
fact(5) // 120
// The result of type inference of fact is : 整数 => 整数
```

### メソッド呼び出し

```
変数 list は new java.util.ArrayList
list->add(1)
list->add(2)
list->add(3)
list->add(4)
println(list)
```

現在の実装ではJavaのオブジェクトに対するメソッド呼び出しのみ可能です。プリミティブ型のボクシングは自動的に行われます。

### ブロック呼び出し

```
変数 add は (x, y) => x + y
println(add(1, 2))
```

ブロック呼び出しは`fun(p1, p2, ..., pn)`のように書けます。`fun`の評価結果はブロックオブジェクトでなければなりません。

### リスト

```
変数 list1 は リスト(1, 2, 3, 4, 5)
println(list1)
```

リストは`リスト(e1, e2, ...,en)`という形で書くことができます。

ぬこでは要素を区切るのために余計な記号を必要としません。たとえば、次のように要素は改行で区切ることもできます。これは他の言語にはあまり見られない特徴です：

```
変数 list2 は リスト(
  1
  2
  3
  4
  5
)
println(list2)
変数 list3 は リスト(
              リスト(1 2 3)
              リスト(4 5 6)
              リスト(7 8 9))
```

リストの型は`List<'a>`型になります。

### 辞書

```
変数 dict1 は 辞書["apple" -> "りんご", "blue" -> "青"]
dic1 「辞書」#get "apple" // => "りんご"
dic1 「辞書」#get "blue"  // => "青"
dic1 「辞書」#get "cat"   // => null
```

辞書は

```
辞書[k1 -> v1, ..., kn ->vn]
```

のように書けます。`kn`と`vn`は式です。リストと同様に改行で要素を区切ることもできます：

```
変数 dic2 は 辞書[
  "apple" -> "りんご"
  "blue"  -> "青"
]
```

辞書の型は`Map<'k, 'v>`になります。

### 集合

集合は`%(v1, ..., vn)` （`vn`は式です）のように書けます。リストと同様に改行やスペースで区切ることもできます：

```
変数 set1 は %(1, 2, 3)
```

```
変数 set2 は %(1 2 3) // カンマは省略
```

```
変数 set3 は %(
  1
  2
  3
)
```

集合の型は`Set<'a>`になります。

### 繰り返し式

繰り返し式は、条件を満たす間、式を評価し続けます。例えば、以下のプログラムは`10`を表示します。

```
変数 i は 0
(i < 10) のあいだ {
  i = i + 1
} をくりかえす
println(i)
```

### 数値リテラル

ぬこではいくつかの数値リテラルがサポートされています。現時点では、`整数`、`バイト`、`小数` のリテラルがサポートされています。

### 整数

```
println(100)
println(200)
println(300)
```

整数リテラルの最大値には制限がありません。

### バイト

バイトリテラルのサフィックスは`BY`です。バイトリテラルの最大値はScalaの`Byte.MaxValue`で、最小値は`Byte.MinValue`です。

```
println(127BY)
println(-127BY)
println(100BY)
```
### 小数

```
println(1.0)
println(1.5)
```

ぬこの小数は10進浮動小数点数ですので直感的に扱うことができます。2進浮動小数点数は将来的に「二進小数」のような型によってサポートする予定です。

### コメント

ぬこでは二種類のコメントを提供しています。

### ネスト可能な複数行コメント

```
1 + /* ネスト
  /* した */ コメント */ 2 // => 3
```

### 行コメント

```
1 + // コメント
    2 // => 3
```

## 型システムと型推論

ぬこは静的型付きプログラミング言語です。ぬこの型システムの特徴は強い型推論があることです。強い型推論があれば型を書かなくてもほとんどの場合に推測してくれます。

```
ブロック fold_left(list) は (z) => (f) => {
  もし (isEmpty(list)) z でなければ　fold_left(tail(list))(f(z, head(list)))(f)
}
// The result of type inference: List<'a> => 'b => (('b, 'a) => 'b) => 'b
```

### 強制型変換

場合によっては型システムからの脱出口が必要なこともあります。このようなケースでユーザーは型変換を強制することで問題を解決できます。

```
変数 s: * は (100 :> *) // 100 は強制的に動的型`*`に変換される。
```

## 組み込みブロック

ぬこはいくつかの組み込みブロックを提供しています。

### 標準入出力ブロック

- `println: (param:Any) => Any`  
  `param`を標準出力に表示します。
 
```
println("Hello, World!")
```

- `printlnError: (param:Any) => Any`  
  `param`を標準エラー出力に表示します。

```
printlnError("Hello, World!")
```

### 文字列操作ブロック

- `substring: (s: 文字列, begin: 整数, end: 整数) => 文章`  
   文字列`s`の部分文字列を返します。部分文字列はインデックス`begin`からインデックス`end` - 1までを切り取った文字列です。

```
substring("FOO", 0, 1) // => "F"
```

- `at: (s:文字列, index:整数) => 文字列`  
  文字列`s`のインデックス`index`番目にある文字を返します。

```
at("BAR", 2) // => "R"
```

- `matches: (s: 文字列, regex: 文字列) => 真偽`  
  文字列`s`が正規表現`regex`にマッチした場合`true`を、そうでない場合`false`を返します。

```
変数 pattern は "[0-9]+"
matches("199", pattern) // => true
matches("a", pattern)   // => false
```

### 数値関係のブロック

- `sqrt: (value:小数) => 小数`  
   `value`の平方根を返します。
 
```
sqrt(2.0) // => 1.4142135623730951
sqrt(9.0) // => 3.0
```
  
- `int: (vaue:小数) => 整数`  
 
小数型の値`value`を整数型に変換します。小数点以下は切り捨てられます。

```
int(3.14159265359) // => 3
```

- `double: (value:整数) => 小数`  
   `value`を小数型に変換します。

```
double(10) // => 10.0
```

- `floor: (value:小数) => 整数`  
  `value`を切り下げた値を返します。

```
floor(1.5) // => 1
floor(-1.5) // => -1
```

- `ceil: (value:小数) => 整数`  
   `value`を切り上げた値を返します。
 
```
ceil(4.4)  // => 5
ceil(4.5)  // => 5
ceil(-4.4) // => -4
ceil(-4.5) // => -4
```

- `abs: (value:小数) => 小数`  
    `value`の絶対値を返します。
 
```
abs(10.5)  // => 10.5
abs(-10.5) // => 10.5
```

### リスト関係のブロック

- `map: (list:List<'a>) => (fun:('a) => 'b) => List<'b>`  
   ブロック`fun`を与えられたリスト`list`のすべての要素に適用した結果からなる新しいリストを返します。
 
```
map([1 2 3])((x) => x + 1) // => [2 3 4]
map([2 3 4]){x => x + 1}   // => [3 4 5]
 ```

- `head: (list:List<'a>) => List<'a>`  
  `list`の最初の要素を返します。
 
```
head([1 2 3 4]) // => 1
```

- `tail: (list:List<'a>) => List<'a>`  
   `list`の最初の要素を除いた新しいリストを返します。
 
```
tail([1 2 3 4]) // => [2 3 4]
```

- `cons: (value:'a) => (list:List<'a>) => List<'a>`  
    `value`と`list`を結合した新しいリストを返します。
 
```
cons(1)([2 3 4]) // => [1 2 3 4]
```

- `size: (list:List<'a>) => 整数`  
   リスト`list`の要素数を返します。
 
```
size([1 2 3 4 5]) // => 5
```

- `isEmpty: (list:List<'a>) => 真偽`
   もし`list`が空ならばtrueを、そうでなければfalseを返します。
 
```
isEmpty([])       // => true
isEmpty([1 2 3])  // => false
```

- `foldLeft: (list:List<'a>) => (acc:'b) => (fun:('b, 'a) => 'b) => 'b`  
    ブロック`fun`を開始値`acc`とリスト`list`のすべての要素に左から右に適用します。
 
```
foldLeft([1 2 3 4])(0)((x, y) => x + y)         // => 10
foldLeft([1.0 2.0 3.0 4.0])(0.0){x, y => x + y} // => 10.0
foldLeft([1.0 2.0 3.0 4.0])(1.0){x, y => x * y} // => 24.0
```

### スレッド関係のブロック

- `thread: (fun:() => 空) => 空` 
    新しいスレッドを作成し、ブロック `fun` を非同期に実行します。
    ```
    thread(() => {
      sleep(1000)
      println("Hello from another thread.")
    })
    println("Hello from main thread.")
    // => "Hello from main thread."
    // => "Hello from another thread."
    ```

- `sleep: (millis: 整数) => 空` 
    現在のスレッドを`millis`ミリ秒の間休眠状態にします。
    ```
    sleep(1000)
    ```

### ユーティリティブロック

- `stopwatch: (fun:() => 空) => 整数`  
   引数で渡された関数`fun`の評価にかかった時間をミリ秒単位で返します。
 
```
変数 time は stopwatch( => {
  sleep(1000)
  println("1")
})
println("it took #{time} milli seconds")
```

- `ToDo: () => 空`  
    評価されると `nuko.runtime.NotImplementedError` を投げます。

 ```
ToDo()  // => throw NotImplementedError
```

### アサーションブロック

- `assert: (condition: 真偽) => 空`  
    Asserts that the `condtion` should be true, and throws `nuko.runtime.AssertionError` if the `condition` is false.
  ```
  assert(2 == 1 + 1)  // => OK
  assert(3 > 5)       // => NG: AssertionError
  ```

- `assertResult: (expected: Any)(actual: Any) => 空`  

    Asserts that the `actual` value should be equal to the `expected` value, and throws `nuko.runtime.AssertionError` if the `actual` value is not equal to the `expected` value.
 
```
変数 add は (x, y) => {
  x + y
}
assertResult(5)(add(2, 3))  // => OK
assertResult(2)(add(1, 2))  // => NG: AssertionError
```