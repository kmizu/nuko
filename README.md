# 日本語プログラミング言語Nuko  [![Build Status](https://github.com/kmizu/nuko/actions/workflows/scala.yml/badge.svg?branch=main)](https://github.com/kmizu/nuko/actions)

Nukoは日本語ぽい記法で記述できるプログラミング言語です。

たとえば、変数を使った簡単なプログラムは以下のように記述することができます：

```
変数 挨拶 は　(時間 < 12) ならば "おはようございます" でなければ "こんにちは"
```

プログラミング言語Nukoは、日本語プログラミング言語を作ってみたくなって思い立って作ったものです。元々は拙作の
プログラミング言語[Klassic](https://klassic/klassic)が元になってるため、Klassicで使えた言語の機能はほとんど利用できます。

以下はNukoの特徴です：

* Hindley-Milnerぽい型システム
* レキシカルスコープ変数
* ファーストクラス関数（いわゆるラムダ式も含む）
* 文字列補間
  * Ruby, Scala, Kotlinなど最近の言語には大体あるやつです
* ループ
  * `{条件式}のあいだ{本体}くりかえす` のように書けます
* 変数宣言
  * `変数xはv` のように書けます
* スペース＆行センシティブなリテラルが使えます
  * リストリテラル
  * マップリテラル
  * セットリテラル
* Java FFI
  * Javaのメソッドを自然な形で呼び出せます

Nukoを使えば、関数型プログラミングを日本語でできる……はずです。たぶん。

## インストール

NukoのインストールにはJava 17以降が必要です。

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
変数 one は 1 [;]
```

変数`one`を宣言し、その初期値を`1`とします。セミコロンは省略することができます。

### 関数リテラル

```
変数 add は (x, y) => x + y
```

変数`add`を宣言し、その初期値を`(x, y) => x + y`とします。このように、関数リテラルは
`(引数) => 式`という形で書きます。関数リテラルは、関数オブジェクトを生成します。

関数リテラルが複数の式に渡る場合以下のように書くことができます：

```
変数 printAndAdd は (x, y) => {
  println(x)
  println(y)
  x + y
}
```

### 関数定義

名前のついた関数を定義したいこともあるでしょう。その場合は、以下のように書きます：

```
関数 fact(n) は もし(n < 2) 1 でなければ n * fact(n - 1)
fact(0) // 1
fact(1) // 1
fact(2) // 2
fact(3) // 6
fact(4) // 24
fact(5) // 120
// The result of type inference of fact is : Int => Int
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

### 関数呼び出し

```
変数 add は (x, y) => x + y
println(add(1, 2))
```

関数呼び出しは`fun(p1, p2, ..., pn)`のように書けます。`fun`の評価結果は関数オブジェクトでなければなりません。

### リストリテラル

```
変数 list1 は [1, 2, 3, 4, 5]
println(list1)
```

リストリテラルは`[e1, e2, ...,en]`という形で書くことができます。Nukoでは要素のセパレータに改行や
スペースを使えます。これは他の言語にはあまり見られない特徴です：

```
変数 list2 は [
  1
  2
  3
  4
  5
]
println(list2)
変数 list3 は [[1 2 3]
              [4 5 6]
              [7 8 9]]
```

リストリテラルの型は`List<'a>`型になります。

### マップリテラル

```
変数 map は %["A": 1, "B": 2]
map Map#get "A" // => 1
map Map#get "B" // => 2
map Map#get "C" // => null
```

マップリテラルは`%[k1:v1, ..., kn:vn]` （`kn`と`vn`は式です）のように書けます。リストリテラルと同様に
セパレータには改行やスペースを使えます：

```
変数 map2 は %[
  "A" : 1
  "b" : 2
]
```

マップリテラルの型は`Map<'k, 'v>`になります。

### 集合リテラル

集合リテラルは`%(v1, ..., vn)` （`vn`は式です）のように書けます。リストリテラルと同様にセパレータには
改行やスペースを使えます：

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

集合リテラルの型は`Set<'a>`になります。

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

Nukoではいくつかの数値リテラルがサポートされています。

### Int

```
println(100)
println(200)
println(300)
```

The max value of Int literals is `Int.MaxValue` in Scala and the min value of integer literals is 
`Int.MinValue` in Scala.

### Byte

The suffix of byte literal is `BY`.  The max value of long literals is `Byte.MaxValue` in Scala and 
the min value of long literals is `Byte.MinValue` in Scala.

```
println(127BY)
println(-127BY)
println(100BY)
```

### Short

The suffix of short literal is `S`.  The max value of long literals is `Short.MaxValue` in Scala and 
the min value of long literals is `Short.MinValue` in Scala.

```
println(100S)
println(200S)
println(300S)
```

### Long

```
println(100L)
println(200L)
println(300L)
```

The suffix of long literal is `L`.  The max value of long literals is `Long.MaxValue` in Scala and 
the min value of long literals is `Long.MinValue` in Scala.

### Double

```
println(1.0)
println(1.5)
```

The max value of double literal is `Double.MaxValue` in Scala and the min value of double literal is `Double.MinValue`
in Scala.

### Float

```
println(1.0F)
println(1.5F)
```

The max value of float literal is `Float.MaxValue` in Scala and the min value of float literal is `Float.MinValue`
in Scala.

### コメント

Nukoでは二種類のコメントを提供しています。

### ネスト可能なブロックコメント

```
1 + /* nested
  /* comment */ here */ 2 // => 3
```

### 行コメント

```
1 + // comment
    2 // => 3
```

## 型システム

Nukoは静的型付き関数型プログラミング言語です。Nukoの型システムの特徴は`制限付き`サブタイピングです。`制限付き`とは、暗黙のアップキャストが許されず、必要な場合は明示的に指定する必要があるということです。

### Hindley-Milner型推論

Nukoの型推論はHindley-Milner型推論に基づいています。そのため、型注釈は多くの場合省略することができます：

```
関数 fold_left(list) は (z) => (f) => {
  もし (isEmpty(list)) z でなければ　fold_left(tail(list))(f(z, head(list)))(f)
}
// The result of type inference: List<'a> => 'b => (('b, 'a) => 'b) => 'b
```

### 型キャスト

In some cases, escape hatches from type system are required. In such cases,
user can insert cast explicitly.

```
変数 s: * は (100 :> *) // 100 is casted to dynamic type ( `*` )
```

## 組み込み関数

Nukoではいくつかの組み込み関数を提供しています。

### 標準入出力関数

- `println: (param:Any) => Any`  
    display the `param` into the standard output.  
    ```
    println("Hello, World!")
    ```

- `printlnError: (param:Any) => Any`  
    display the `param` into the standard error.  
    ```
    printlnError("Hello, World!")
    ```

### 文字列関数

- `substring: (s:String, begin:Int, end:Int) => String`  
    Returns a substring of the String `s`. The substring begins at the index `begin` and ends at the index `end` - 1.  
    ```
    substring("FOO", 0, 1) // => "F"
    ```

- `at: (s:String, index:Int) => String`  
    Returns a String with a character value at the index `index` of the String `s`.  
    ```
    at("BAR", 2) // => "R"
    ```

- `matches: (s:String, regex:String) => Boolean`  
    Returns true if the String `s` matches the regular expression `regex`, false otherwise.  
    ```
    変数 pattern は "[0-9]+"
    matches("199", pattern) // => true
    matches("a", pattern)   // => false
    ```

### 数値関係の関数

- `sqrt: (value:Double) => Double`  
    Returns the square root of the Double `value`.
    ```
    sqrt(2.0) // => 1.4142135623730951
    sqrt(9.0) // => 3.0
    ```
- `int: (vaue:Double) => Int`  
    Returns the Double `value` as the Int value.
    ```
    int(3.14159265359) // => 3
    ```

- `double: (value:Int) => Double`  
    Returns the Int `value` as the Double value.  
    ```
    double(10) // => 10.0
    ```

- `floor: (value:Double) => Int`  
    `value`を切り下げた値を返します。
    ```
    floor(1.5) // => 1
    floor(-1.5) // => -1
    ```

- `ceil: (value:Double) => Int`  
    `value`を切り上げた値を返します。
      ```
      ceil(4.4)  // => 5
      ceil(4.5)  // => 5
      ceil(-4.4) // => -4
      ceil(-4.5) // => -4
      ```

- `abs: (value:Double) => Double`  
    `value`の絶対値を返します。
    ```
    abs(10.5)  // => 10.5
    abs(-10.5) // => 10.5
    ```

### リスト関係の関数

- `map: (list:List<'a>) => (fun:('a) => 'b) => List<'b>`  
    Returns a new List consisting of the results of applying the given function `fun` to the elements of the given List `list`.
    ```
    map([1 2 3])((x) => x + 1) // => [2 3 4]
    map([2 3 4]){x => x + 1}   // => [3 4 5]
    ```

- `head: (list:List<'a>) => List<'a>`  
  Returns the first element of the List `list`.
  ```
  head([1 2 3 4]) // => 1
  ```

- `tail: (list:List<'a>) => List<'a>`  
    Returns a new List consisting of the elements of the given List `list` except for the first element.
    ```
    tail([1 2 3 4]) // => [2 3 4]
    ```

- `cons: (value:'a) => (list:List<'a>) => List<'a>`  
    Creates a new List, the head of which is `value` and the tail of which is `list`.  
    ```
    cons(1)([2 3 4]) // => [1 2 3 4]
    ```

- `size: (list:List<'a>) => Int`  
    Returns the size of the List `list`.  
    ```
    size([1 2 3 4 5]) // => 5
    ```

- `isEmpty: (list:List<'a>) => Boolean`  
    Returns true if the List `list` is empty, false otherwise.  
    ```
    isEmpty([])       // => true
    isEmpty([1 2 3])  // => false
    ```

- `foldLeft: (list:List<'a>) => (acc:'b) => (fun:('b, 'a) => 'b) => 'b`  
    Applies a function `fun` to a start value `acc` and all elements of the List `list`, going left to right.
    ```
    foldLeft([1 2 3 4])(0)((x, y) => x + y)         // => 10
    foldLeft([1.0 2.0 3.0 4.0])(0.0){x, y => x + y} // => 10.0
    foldLeft([1.0 2.0 3.0 4.0])(1.0){x, y => x * y} // => 24.0
    ```

### スレッド関係の関数

- `thread: (fun:() => Unit) => Unit` 
    新しいスレッドを作成し、引数の関数 `fun` を非同期に実行します。
    ```
    thread(() => {
      sleep(1000)
      println("Hello from another thread.")
    })
    println("Hello from main thread.")
    // => "Hello from main thread."
    // => "Hello from another thread."
    ```

- `sleep: (millis:Int) => Unit` 
    現在のスレッドを`millis`ミリ秒の間休眠状態にします。
    ```
    sleep(1000)
    ```

### ユーティリティ関数

- `stopwatch: (fun:() => Unit) => Int`  
    Returns the time in milliseconds taken to evaluate the passed argument function `fun`.
    ```
    変数 time は stopwatch( => {
      sleep(1000)
      println("1")
    })
    println("it took #{time} milli seconds")
    ```

- `ToDo: () => Unit`  
    Throws `nuko.runtime.NotImplementedError` when evaluated.
    ```
    ToDo()  // => throw NotImplementedError
    ```

### アサーション関数

- `assert: (condition:Boolean) => Unit`  
    Asserts that the `condtion` should be true, and throws `nuko.runtime.AssertionError` if the `condition` is false.
  ```
  assert(2 == 1 + 1)  // => OK
  assert(3 > 5)       // => NG: AssertionError
  ```

- `assertResult: (expected:Any)(actual:Any) => Unit`  
    Asserts that the `actual` value should be equal to the `expected` value, and throws `nuko.runtime.AssertionError` if the `actual` value is not equal to the `expected` value.
    ```
    変数 add は (x, y) => {
      x + y
    }
    assertResult(5)(add(2, 3))  // => OK
    assertResult(2)(add(1, 2))  // => NG: AssertionError
    ```

### Interoperating Functions

- `url: (value:String) => java.net.URL`  
    Creates new `java.net.URL` object from a String `value`.
    ```
    url("https://github.com/kmizu/nuko")
    ```

- `uri: (value:String) => java.net.URI`  
    Creates new `java.net.URI` object from a String `value`.
    ```
    uri("https://github.com/kmizu/nuko")
    ```

- `desktop: () => java.awt.Desktop`  
    Returns the Desktop instance of the current browser context via Java Desktop API.
    ```
    desktop()->browse(uri("https://github.com/kmizu/nuko"))
    ```