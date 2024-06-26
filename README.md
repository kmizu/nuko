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
  * 繰り返しを `{条件式} のあいだ {本体} くりかえす` のように書けます
  * 条件分岐を `{条件式} ならば　{本体} でなければ {本体}` のように書けます
  * `変数xはv` のように変数宣言ができます
* 各種データ構造が簡単に使えます
  * リスト: `リスト(1, 2, 3, 4, 5)`
  * 辞書: `辞書("apple" -> "りんご", "blue" -> "青")`
  * 集合: `集合(1, 2, 3, 4, 5)`
* 関数型プログラミング言語としての機能を持っています
  * ですが、ぬこで関数型プログラミングを意識する必要はありません
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

次のようなプログラムを`hello.nk`という名前で保存してください：

```
表示("Hello, World!")
```

このプログラムを実行するには、`java -jar nuko.jar hello.nk`とします：

```console
$ java -jar nuko.jar hello.nk

Hello, World!
```

## 構文

### 変数宣言

```
変数 一 は 1 
```

変数`one`を宣言し、その初期値を`1`とします。

### 無名関数

```
変数 加算 は (x, y) => x + y
```

変数`add`を宣言し、その初期値を`(x, y) => x + y`とします。無名関数は
`(引数) => 式`という形で書きます。無名関数は、関数オブジェクトを生成します。

無名関数の本体が複数の式に渡る場合、以下のように書けます：

```
変数 出力して加算 は (x, y) => {
  表示(x)
  表示(y)
  x + y
}
```

### 構造体

複数のデータ型を組み合わせてより複雑なデータを作り上げるために、構造体を使えます。以下は名簿を表す構造体の宣言です。

```
構造体 名簿 {
  名前: 文字列
  年齢: 整数
}

変数 名簿 は @名簿("水島宏太", 40)
一致を確認（"水島宏太"）(名簿#名前） // => OK
```

### 関数定義

名前のついた関数を定義したいこともあるでしょう。その場合は、以下のように書きます：

```
関数 階乗(n) は もし(n < 2) 1 でなければ (n * 階乗(n - 1))
階乗(0) // 1
階乗(0) // 1
階乗(1) // 1
階乗(2) // 2
階乗(3) // 6
階乗(4) // 24
階乗(5) // 120
```

### 関数呼び出し

```
変数 加算 は (x, y) => x + y
表示(加算(1, 2))
```

関数呼び出しは`F(p1, p2, ..., pn)`のように書けます。`F`の評価結果は関数オブジェクトでなければなりません。

### リスト

```
変数 list1 は リスト(1, 2, 3, 4, 5)
表示(list1)
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
表示(list2)
変数 list3 は リスト(
              リスト(1 2 3)
              リスト(4 5 6)
              リスト(7 8 9))
```

リストの型は`List<'a>`型になります。

### 辞書

```
変数 英和辞典 は 辞書("apple" → "りんご" "blue" → "青")
英和辞典 値を取得 "apple" // りんご
英和辞典 値を取得 "blue" // 青
英和辞典 値を取得 "cat" // null
```

辞書は

```
辞書(k1 → v1, ..., kn → vn)
```

のように書けます。`kn`と`vn`は式です。リストと同様に改行で要素を区切ることもできます：

```
変数 dic2 は 辞書(
  "apple" → "りんご"
  "blue"  → "青"
)
```

辞書の型は`辞書<'k, 'v>`になります。

### 集合

集合は`集合(要素1, ..., 要素n)`のように書けます。リストと同様に改行やスペースで区切ることもできます：

```
変数 集合１ は 集合(1, 2, 3)
表示(集合1)
```

```
変数 集合２ は 集合(1 2 3) // カンマは省略できる
表示(集合2)
```

```
変数 集合３ は 集合(
  1
  2
  3
) // 改行で区切ることもできる
```

集合の型は`集合<'a>`になります。

### 代入式

代入式は宣言済みの変数に新しい値を代入します。代入には`←`または`<-`を使います。

```
変数 カウンタ は 0
カウンタ ← カウンタ + 1
表示(カウンタ) // 1 が表示される
```

### 繰り返し式

繰り返し式は、条件を満たす間、式を評価し続けます。例えば、以下のプログラムは`55`を表示します。

```
変数 合計 は 0
変数 カウンタ は 0
(カウンタ < 10) のあいだ {
  カウンタ ← カウンタ + 1
  合計 ← 合計 + カウンタ
} をくりかえす
表示(合計)
```

### 数値リテラル

ぬこではいくつかの数値リテラルがサポートされています。現時点では、`整数`、`バイト`、`小数` のリテラルがサポートされています。

### 整数

```
表示(100)
表示(200)
表示(300)
```

整数リテラルの最大値には制限がありません。

### バイト

バイトリテラルのサフィックスは`BY`です。バイトリテラルの最大値は`127`で、最小値は`-128`です。

```
表示(127BY)
表示(-127BY)
表示(100BY)
```
### 小数

```
表示(1.0)
表示(1.5)
```

ぬこの小数は10進浮動小数点数なので、直感的に扱うことができます。

2進浮動小数点数は将来的に「二進小数」のような型によってサポートする予定です。

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

ぬこは静的な型をもつプログラミング言語です。静的な型があることでプログラムのミスを早めに検出することができます。

```
関数 左からの畳み込み（リスト１）は (累積値) => (関数F) => {
  もし (isEmpty(リスト１)) 累積値 でなければ 左からの畳み込み(末尾(リスト１))(関数F(累積値, 先頭(リスト１)))(関数F)
}
```

### 強制型変換

場合によっては型が窮屈なこともあります。ユーザーは型変換を強制する演算子`:>`を使うことで問題を解決できます。

```
変数 s: 万物 は (100 :> 万物) // 100 は強制的に型「万物」に変換される。
```

## 組み込み関数

ぬこはいくつかの組み込み関数を提供しています。

### 標準入出力関数

- `表示: (値: 万物) => 万物`  
 
`値`を標準出力に表示します。
 
```
表示("Hello, World!")
```

- `エラー表示: (値: 万物) => 万物`  
 
`値`を標準エラー出力に表示します。

```
エラー表示("Hello, World!")
```

### 文字列操作関数

- `部分文字列: (S: 文字列, 開始: 整数, 終了: 整数) => 文字列`  
 
文字列`S`の部分文字列を返します。部分文字列はインデックス`開始`からインデックス`終了 - 1`までを切り取った文字列です。

```
部分文字列("FOO", 0, 1) // => "F"
```

- `文字を取得: (S:文字列, 添字: 整数) => 文字列`  
 
文字列`S`の`添字`番目にある文字を返します。

```
文字を取得("BAR", 2) // => "R"
```

- `マッチする: (文字列A: 文字列, パターン: 文字列) => 真偽`  
 
`文字列A`が正規表現`パターン`にマッチした場合`true`を、そうでない場合`false`を返します。

```
変数 パターン は "[0-9]+"
マッチする("199", パターン) // => true
マッチする("a", パターン)   // => false
```

これは中置記法で次のように書いても同じ意味です。

```a
変数 パターン は "[0-9]+"
"199" マッチする パターン // => true
"a" マッチする パターン   // => false
```

### 数値関係の関数

- `平方根: (value:小数) => 小数`  

`値`の平方根を返します。
 
```
平方根(2.0) // => 1.4142135623730951
平方根(9.0) // => 3.0
```
  
- `整数: (値: 小数) => 整数`  
 
小数型の`値`を整数型に変換します。小数点以下は切り捨てられます。

```
整数(3.14159265359) // => 3
```

- `小数: (値: 整数) => 小数`  
 
`値`を小数型に変換します。

```
小数(10) // => 10.0
```

- `切り下げ: (値: 小数) => 整数`  
 
`値`を切り下げた値を返します。

```
切りさげ(1.5) // => 1
切り下げ(-1.5) // => -1
```

- `切り上げ: (値 :小数) => 整数`  

小数の`値`を切り上げた値を返します。
 
```
切り上げ(4.4)  // => 5
切り上げ(4.5)  // => 5
切り上げ(-4.4) // => -4
切り上げ(-4.5) // => -4
```

- `絶対値: (値: 小数) => 小数`  

`値`の絶対値を返します。
 
```
絶対値(10.5)  // => 10.5
絶対値(-10.5) // => 10.5
```

### リスト関係の関数

- `変換: (list: リスト<'a>) => (fun:('a) => 'b) => リスト<'b>`  

関数`fun`を与えられたリスト`list`のすべての要素に適用した結果からなる新しいリストを返します。
 
```
変換(リスト(1 2 3))((x) => x + 1) // => [2 3 4]
変換(リスト(2 3 4)){x => x + 1}   // => [3 4 5]
 ```

- `先頭: (リストA: リスト<'a>) => リスト<'a>`  

`リストA`の最初の要素を返します。
 
```
先頭(リスト(1 2 3 4)) // => 1
```

- `末尾: (リストA: リスト<'a>) => リスト<'a>`  

`リストA`の最初の要素を除いた新しいリストを返します。
 
```
末尾(リスト(1 2 3 4)) // => [2 3 4]
```

- `構築: (値A: 'a) => (リストA: リスト<'a>) => リスト<'a>`  

`値A`と`リストA`を結合した新しいリストを返します。
 
```
構築(1)(リスト(2 3 4)) // => [1 2 3 4]
1 構築 リスト(2 3 4)   // => [1 2 3 4]
```

- `サイズ: (リストA: リスト<'a>) => 整数`  
 
`リストA`の要素数を返します。
 
```
サイズ(リスト(1 2 3 4 5)) // => 5
```

- `空である: (リストA: リスト<'a>) => 真偽`
 
もし`リストA`が空ならば`true`を、そうでなければ`false`を返します。
 
```
空である(リスト())       // => true
空である(リスト(1 2 3))  // => false
```

- `たたむ: (list: リスト<'a>) => (acc:'b) => (fun:('b, 'a) => 'b) => 'b`  
 
関数`fun`を開始値`acc`とリスト`list`のすべての要素に左から右に適用します。
 
```
たたむ([1 2 3 4])(0)((x, y) => x + y)         // => 10
たたむ([1.0 2.0 3.0 4.0])(0.0){x, y => x + y} // => 10.0
たたむ([1.0 2.0 3.0 4.0])(1.0){x, y => x * y} // => 24.0
```

### スレッド関係の関数

- `スレッド開始: (fun:() => 空) => 空` 
    新しいスレッドを作成し、関数 `fun` を非同期に実行します。
    ```
    スレッド開始(() => {
      休眠する(1000)
      表示("Hello from another thread.")
    })
    表示("Hello from main thread.")
    // => "Hello from main thread."
    // => "Hello from another thread."
    ```

- `休眠する: (時間: 整数) => 空` 
 
現在のスレッドを`時間`ミリ秒の間休眠状態にします。

```
休眠する(1000) // 1000ミリ秒 = 1秒休眠
 ```

### ファイル関数

- `ファイル#読み込む: (パス名: 文字列) => 文字列`

`パス名`で表されるファイルを読み込み、その内容を文字列として返します。

```
ファイル#読み込む("test.txt") // test.txtの内容を読み込む
```

- `ファイル#書き込む: (パス名: 文字列) => (内容: 文字列) => 空`

`パス名`で表されるファイルに`内容`を書き込みます。

```
ファイル#書き込む("test.txt")("Hello, World!")
```

### ユーティリティ関数

- `時間を計測する: (fun:() => 空) => 整数`  
   引数で渡された関数`fun`の評価にかかった時間をミリ秒単位で返します。
 
```
変数 経過時間 は 時間を計測する( => {
  休眠する(1000)
  表示("1")
})
表示("#{経過時間} ミリ秒経過しました")
```

- `後で埋める: () => 空`  
 
評価されるとプログラムが終了します。まだ実装がないけど、後で埋めることを示すために使います。

 ```
後で埋める()  // プログラムが終了
```

### 確認関数

何かの式がある値であることを確認します。確認に失敗するとその場でプログラムが終了します。これらの関数はプログラムのバグを早めに見つけるのに役立ちます。

- `確認: (条件: 真偽) => 空`  

`条件`が`true`かどうかを確認します。もし`false`ならプログラムはその場で終了します。

```
確認(2 == 1 + 1)  // => OK
確認(3 > 5)       // => NG。プログラムはその場で終了
 ```

- `一致を確認: (期待する値: 万物)(実際の値: 万物) => 空`  

`期待する値`が`実際の値`と一致することを確認します。一致しなければプログラムはその場で終了します。
 
```
変数 加算 は (x, y) => {
  x + y
}
一致を確認(5)(加算(2, 3))  // => OK
一致を確認(2)(加算(1, 2))  // => NG。プログラムはその場で終了
```
