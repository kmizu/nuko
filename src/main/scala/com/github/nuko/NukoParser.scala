package com.github.nuko


import scala.util.matching.Regex
import com.github.nuko.Ast._
import com.github.nuko.Type._
import com.github.nuko.{Location => NukoLocation}
import com.github.kmizu.scomb
import com.github.kmizu.scomb.{Result, SCombinator}

import scala.collection.mutable

/**
 * @author Kota Mizushima
 */
class NukoParser extends Processor[String, Program, InteractiveSession] {
  object Core extends SCombinator {
    object Klassic {
      def publicLocations: mutable.Map[Int, scomb.Location] = locations

      implicit def stringToParser(literal: String): Parser[String] = $(literal)

      implicit def regexToParser(literal: Regex): Parser[String] = regularExpression(literal)

      def %% : Parser[SourceLocation] = % ^^ { l =>
        SourceLocation(l.line, l.column)
      }

      def commit[T](parser: Parser[T]): Parser[T] = parser.commit

      lazy val LINEFEED: Parser[String] = ("\r\n" | "\r" | "\n")

      lazy val SEMICOLON: Parser[String] = ";"

      lazy val ANY: Parser[String] = any ^^ {
        _.toString
      }

      lazy val SPACING: Parser[String] = rule {
        (COMMENT | "\r\n" | "\r" | "\n" | " " | "　" | "\t" | "\b" | "\f" | "　").* ^^ {
          _.mkString
        }
      }

      lazy val SPACING_WITHOUT_LF: Parser[String] = rule {
        (COMMENT | "\t" | " " | "　" | "\b" | "\f").* ^^ {
          _.mkString
        }
      }

      lazy val TERMINATOR: Parser[String] = rule {
        (LINEFEED | SEMICOLON | EOF) << SPACING
      }

      lazy val SEPARATOR: Parser[String] = rule {
        (LINEFEED | COMMA | EOF | SPACING_WITHOUT_LF) << SPACING
      }

      lazy val BLOCK_COMMENT: Parser[Any] = rule {
        "/*" ~ (not("*/") ~ (BLOCK_COMMENT | ANY)).* ~ "*/"
      }

      lazy val LINE_COMMENT: Parser[Any] = rule {
        "//" ~ (not(LINEFEED) ~ ANY).* ~ LINEFEED
      }

      lazy val COMMENT: Parser[Any] = rule {
        BLOCK_COMMENT | LINE_COMMENT
      }

      def CL[T](parser: Parser[T]): Parser[T] = parser << SPACING

      private[this] val tokenNames = mutable.Set.empty[String]

      /**
        * Define a *keywork token*, which is regarded as a keyword
        * Note that this method must not called lazily since this method
        * has side effects.
        * @param name token name. it must not have spaces
        */
      def kwToken(name: String): Parser[String] = {
        tokenNames += name
        name << SPACING_WITHOUT_LF
      }

      /**
        * Define a *special token*, which is *not* regarded as a keyword
        * @param parser
        * @return
        */
      def spToken(parser: String): Parser[String] = parser << SPACING_WITHOUT_LF

      def unescape(input: String): String = {
        val builder = new java.lang.StringBuilder
        val length = input.length
        var i = 0
        while (i < length - 1) {
          (input.charAt(i), input.charAt(i + 1)) match {
            case ('\\', 'r') => builder.append('\r'); i += 2
            case ('\\', 'n') => builder.append('\n'); i += 2
            case ('\\', 'b') => builder.append('\b'); i += 2
            case ('\\', 'f') => builder.append('\f'); i += 2
            case ('\\', 't') => builder.append('\t'); i += 2
            case ('\\', '\\') => builder.append('\\'); i += 2
            case (ch, _) => builder.append(ch); i += 1
          }
        }
        if (i == length - 1) {
          builder.append(input.charAt(i))
        }
        new String(builder)
      }

      //begin token definition
      val LT: Parser[String] = kwToken("<")
      val GT: Parser[String] = kwToken(">")
      val LTE: Parser[String] = kwToken("<=")
      val GTE: Parser[String] = kwToken(">=")
      val DICTIONARY_BEGIN: Parser[String] = kwToken("辞書(") | kwToken("辞書（")
      val DICTIONARY_SEPARATOR: Parser[String] = kwToken("→") | kwToken("->")
      val LIST_BEGIN: Parser[String] = kwToken("リスト(")
      val SET_BEGIN: Parser[String] = kwToken("集合(")
      val UNDERSCORE: Parser[String] = kwToken("_")
      val PLUS: Parser[String] = kwToken("+") | kwToken("＋")
      val MINUS: Parser[String] = kwToken("-") | kwToken("－")
      val ASTER: Parser[String] = kwToken("*") | kwToken("＊")
      val SLASH: Parser[String] = kwToken("/") | kwToken("／")
      val LPAREN: Parser[String] = kwToken("(") | kwToken("（")
      val RPAREN: Parser[String] = kwToken(")") | kwToken("）")
      val LBRACE: Parser[String] = kwToken("{") | kwToken("｛")
      val RBRACE: Parser[String] = kwToken("}") | kwToken("｝")
      val LBRACKET: Parser[String] = kwToken("[") | kwToken("［")
      val RBRACKET: Parser[String] = kwToken("]") | kwToken("］")
      val SHARP: Parser[String] = kwToken("#") | kwToken("＃")
      val ATMARK: Parser[String] = kwToken("@") | kwToken("＠")
      val IF: Parser[String] = kwToken("もし")
      val VISUALIZE: Parser[String] = kwToken("視覚化") | kwToken("visualize")
      val ELSE: Parser[String] = kwToken("でなければ")
      val THEN: Parser[String] = kwToken("ならば")
      val WHILE: Parser[String] = kwToken("のあいだ")
      val REPEAT: Parser[String] = kwToken("をくりかえす")
      val IMPORT: Parser[String] = kwToken("import")
      val ENUM: Parser[String] = kwToken("enum")
      val TRUE: Parser[String] = kwToken("true") | kwToken("真")
      val FALSE: Parser[String] = kwToken("false") | kwToken("偽")
      val IN: Parser[String] = kwToken("in")
      val COMMA: Parser[String] = kwToken(",")
      val DOT: Parser[String] = kwToken(".") | kwToken("．")
      val RECORD: Parser[String] = kwToken("構造体")
      val DEF: Parser[String] = kwToken("関数")
      val VARIABLE: Parser[String] = kwToken("変数")
      val EQ: Parser[String] = kwToken("=")
      val JP_HA: Parser[String] = kwToken("は")
      val JP_KIND_OF: Parser[String] = kwToken("の種類は")
      val PLUS_ASSIGN: Parser[String] = kwToken("+<-") | kwToken("+←")
      val MINUS_ASSIGN: Parser[String] = kwToken("-<-") | kwToken("-←")
      val MULT_ASSIGN: Parser[String] = kwToken("*<-") | kwToken("*←")
      val DIV_ASSIGN: Parser[String] = kwToken("/<-") | kwToken("/←")
      val TO: Parser[String]           = kwToken("を")
      val INCREMENT_BY: Parser[String] = kwToken("増やす")
      val DECREMENT_BY: Parser[String] = kwToken("減らす")
      val COLONGT: Parser[String] = kwToken(":>")
      val EQEQ: Parser[String] = kwToken("==")
      val LARROW: Parser[String] = kwToken("<-") | kwToken("←")
      val ARROW1: Parser[String] = kwToken("=>") | kwToken("⇒")
      val ARROW2: Parser[String] = kwToken("->") | kwToken("→")
      val COLON: Parser[String] = kwToken(":")
      val NEW: Parser[String] = kwToken("new")
      val QUES: Parser[String] = kwToken("?")
      val AMP2: Parser[String] = kwToken("&&") | kwToken("かつ")
      val BAR2: Parser[String] = kwToken("||") | kwToken("または")
      val BAR: Parser[String] = kwToken("|")
      val AMP: Parser[String] = kwToken("&")
      val HAT: Parser[String] = kwToken("^")

      // end token definition

      // begin Japanese token definition
      val JP_MULTIPLY: Parser[String] = "掛ける"
      val JP_ADD:      Parser[String] = "足す"
      val JP_SUBTRACT: Parser[String] = "引く"
      val JP_DIVIDE:   Parser[String] = "割る"

      lazy val KEYWORDS: Set[String] = tokenNames.toSet

      lazy val typeAnnotation: Parser[Type] = (JP_KIND_OF | COLON) >> typeDescription

      lazy val castType: Parser[Type] = typeDescription

      def isBuiltinType(name: String): Boolean = name match {
        case "Byte" => true
        case "Short" => true
        case "Int" => true
        case "Long" => true
        case "Float" => true
        case "Double" => true
        case "Boolean" => true
        case "Unit" => true
        case "String" => true
        case _ => false
      }

      lazy val typeVariable: Parser[TVariable] = qident ^^ { id => TVariable(id) }

      lazy val typeDescription: Parser[Type] = rule(
        kwToken("バイト") ^^ { _ => TByte }
      | kwToken("整数") ^^ { _ => TInt }
      | kwToken("小数") ^^ { _ => TReal }
      | kwToken("真偽") ^^ { _ => TBoolean }
      | kwToken("空") ^^ { _ => TUnit }
      | kwToken("文字列") ^^ { _ => TString }
      | kwToken("万物") ^^ { _ => TDynamic }
      | qident ^^ { id => TVariable(id) }
      | ((CL(LPAREN) >> typeDescription.repeat0By(CL(COMMA)) << CL(RPAREN)) << CL(ARROW1)) ~ typeDescription ^^ { case args ~ returnType => TFunction(args, returnType) }
      | (SHARP >> sident).filter { s => !isBuiltinType(s) } ~ (CL(LT) >> typeDescription.repeat0By(CL(COMMA)) << CL(GT)).? ^^ {
          case name ~ Some(args) => TRecordReference(name, args)
          case name ~ None => TRecordReference(name, Nil)
        }
      | sident.filter { s => !isBuiltinType(s) } ~ (CL(LT) >> typeDescription.repeat0By(CL(COMMA)) << CL(GT)).? ^^ {
          case name ~ Some(args) => TConstructor(name, args)
          case name ~ None => TConstructor(name, Nil)
        }
      )

      def root: Parser[Program] = rule(program)

      lazy val program: Parser[Program] = rule {
        (SPACING >> %%) ~ `import`.repeat0By(TERMINATOR) ~ record.repeat0By(TERMINATOR) ~ lines << EOF ^^ {
          case location ~ imports ~ records ~ block => Program(location=location, imports=imports, records=records, block=block)
        }
      }

      lazy val `import`: Parser[Import] = rule {
        (%% << CL(IMPORT)) ~ fqcn.commit ^^ { case location ~ fqcn =>
          val fragments = fqcn.split(".")
          Import(location, fragments(fragments.length - 1), fqcn)
        }
      }

      lazy val record: Parser[RecordDeclaration] = rule {
        for {
          location <- %%
          _ <- RECORD
          name <- commit(sident)
          ts <- commit((CL(LT) >> typeVariable.repeat1By(CL(COMMA)) << CL(GT)).?)
          _ <- commit(CL(LBRACE))
          members <- commit(((sident ~ CL(typeAnnotation << SEMICOLON.?)) ^^ { case n ~ t => (n, t) }).*)
          methods <- commit(methodDefinition.repeat0By(TERMINATOR))
          _ <- commit(RBRACE)
        } yield RecordDeclaration(location, name, ts.getOrElse(Nil), members, methods)
      }

      //lines ::= line {TERMINATOR expr} [TERMINATOR]
      lazy val lines: Parser[Block] = rule {
        SPACING >> (%% ~ line.repeat0By(TERMINATOR) << TERMINATOR.? ^^ { case location ~ expressions =>
          Block(location, expressions)
        })
      }

      //line ::= expression | valDeclaration | functionDefinition
      lazy val line: Parser[Ast.Node] = rule(valDeclaration | functionDefinition | expression)

      //expression ::= ifExpression | whileExpression | assignment | jpAssignment | ternary
      lazy val expression: Parser[Ast.Node] = rule(ifExpression | whileExpression | assignment | jpAssignment | ternary)

      //ifExpression ::= "if" "(" expression ")" expression "else" expression
      lazy val ifExpression: Parser[Ast.Node] = rule {
        (%% << CL(IF) << CL(LPAREN)) ~ commit(expression ~ CL(RPAREN) ~ expression ~ CL(ELSE) ~ expression) ^^ {
          case location ~ (condition ~ _ ~ positive ~ _ ~ negative) => IfExpression(location, condition, positive, negative)
        }
      }

      //whileExpression ::= expression のあいだ expression をくりかえす
      lazy val whileExpression: Parser[Ast.Node] = rule {
        %% ~ (ternary << CL(WHILE)) ~ commit(expression << REPEAT) ^^ {
          case location ~ condition ~ body => WhileExpression(location, condition, body)
        }
      }

      lazy val ternary: Parser[Ast.Node] = rule {
        for(
          location <- %%;
          condition <- infix;
          opt <- ((CL(THEN) ~> infix) ~ (CL(ELSE) ~> infix)).?
        ) yield opt match {
          case Some(th ~ el) => TernaryExpression(location, condition, th, el)
          case None => condition
        }
      }

      lazy val infix: Parser[Ast.Node] = rule {
        chainl(logicalOr)(
         (%% ~ CL(operator)) ^^ { case location ~ op => (lhs: Ast.Node, rhs: Ast.Node) =>
           RecordCall(location, lhs, op, List(rhs))
         }
        )
      }

      lazy val logicalOr: Parser[Ast.Node] = rule {
        chainl(logicalAnd)(
          (%% << CL(BAR2)) ^^ { location => (lhs: Ast.Node, rhs: Ast.Node) => BinaryExpression(location, Operator.BAR2, lhs, rhs) }
        )
      }

      lazy val logicalAnd: Parser[Ast.Node] = rule {
        chainl(bitOr)(
          (%% << CL(AMP2)) ^^ { location => (lhs: Ast.Node, rhs: Ast.Node) => BinaryExpression(location, Operator.AND2, lhs, rhs) }
        )
      }

      lazy val bitOr: Parser[Ast.Node] = rule {
        chainl(bitXor)(
          (%% << CL(BAR)) ^^ { location => (lhs: Ast.Node, rhs: Ast.Node) => BinaryExpression(location, Operator.OR, lhs, rhs) }
        )
      }

      lazy val bitXor: Parser[Ast.Node] = rule {
        chainl(bitAnd)(
          (%% << CL(HAT)) ^^ { location => (lhs: Ast.Node, rhs: Ast.Node) => BinaryExpression(location, Operator.XOR, lhs, rhs) }
        )
      }

      lazy val bitAnd: Parser[Ast.Node] = rule {
        chainl(conditional)(
          (%% << CL(AMP)) ^^ { location => (lhs: Ast.Node, rhs: Ast.Node) => BinaryExpression(location, Operator.AND, lhs, rhs) }
        )
      }

      //conditional ::= add {"==" add | "<=" add | "=>" add | "<" add | ">" add}
      lazy val conditional: Parser[Ast.Node] = rule {
        chainl(add)(
          (%% << CL(EQEQ)) ^^ { location => (left: Ast.Node, right: Ast.Node) => BinaryExpression(location, Operator.EQUAL, left, right) } |
            (%% << CL(LTE)) ^^ { location => (left: Ast.Node, right: Ast.Node) => BinaryExpression(location, Operator.LESS_OR_EQUAL, left, right) } |
            (%% << CL(GTE)) ^^ { location => (left: Ast.Node, right: Ast.Node) => BinaryExpression(location, Operator.GREATER_EQUAL, left, right) } |
            (%% << CL(LT)) ^^ { location => (left: Ast.Node, right: Ast.Node) => BinaryExpression(location, Operator.LESS_THAN, left, right) } |
            (%% << CL(GT)) ^^ { location => (left: Ast.Node, right: Ast.Node) => BinaryExpression(location, Operator.GREATER_THAN, left, right) }
        )
      }

      //add ::= term {("+" | "足す") term | ("-" | "引く" term}
      lazy val add: Parser[Ast.Node] = rule {
        chainl(term)(
          (%% << CL(PLUS | JP_ADD)) ^^ { location => (left: Ast.Node, right: Ast.Node) => BinaryExpression(location, Operator.ADD, left, right) } |
          (%% << CL(MINUS | JP_SUBTRACT)) ^^ { location => (left: Ast.Node, right: Ast.Node) => BinaryExpression(location, Operator.SUBTRACT, left, right) }
        )
      }

      //term ::= factor {("*" | "掛ける") factor | ("/" | "割る") factor}
      lazy val term: Parser[Ast.Node] = rule {
        chainl(unary)(
          (%% << CL(ASTER | JP_MULTIPLY)) ^^ { location => (left: Ast.Node, right: Ast.Node) => BinaryExpression(location, Operator.MULTIPLY, left, right) } |
          (%% << CL(SLASH | JP_DIVIDE)) ^^ { location => (left: Ast.Node, right: Ast.Node) => BinaryExpression(location, Operator.DIVIDE, left, right) }
        )
      }

      lazy val unary: Parser[Ast.Node] = rule(
        %% ~ CL(MINUS) ~ unary ^^ { case location ~ _ ~ operand => MinusOp(location, operand) }
       | %% ~ CL(PLUS) ~ unary ^^ { case location ~ _ ~ operand => PlusOp(location, operand) }
       | invocation
      )

      lazy val invocation: Parser[Ast.Node] = rule(%% ~ recordSelect ~ ((CL(ARROW2) >> ident) ~ (CL(LPAREN) >> expression.repeat0By(CL(COMMA)) << RPAREN).?).* ^^ {
        case location ~ self ~ Nil =>
          self
        case location ~ self ~ npList =>
          npList.foldLeft(self) { case (self, name ~ params) => MethodCall(location, self, name.name, params.getOrElse(Nil)) }
      })

      lazy val recordSelect: Parser[Ast.Node] = rule(%% ~ application ~ ((CL(DOT) >> (%% ~ ident ~ (CL(LPAREN) >> expression.repeat0By((COMMA)) << RPAREN).?))).* ^^ {
        case location ~ self ~ names =>
          val ns = names.map {
            case l ~ n ~ Some(params) => (l, n.name, Some(params))
            case l ~ n ~ None => (l, n.name, None)
          }
          ns.foldLeft(self) { case (e, (l, n, optParams)) =>
            optParams match {
              case Some(params) => RecordCall(l, e, n, params)
              case None => RecordSelect(l, e, n)
            }
          }
      })

      lazy val application: Parser[Ast.Node] = rule {
        %% ~ pipelinable ~ (
          blockFunctionParameter
            | parenthesizedParameter
          ).* ^^ {
          case location ~ f ~ params =>
            params.foldLeft(f: Ast.Node) { (f, params) =>
              FunctionCall(location, f, params)
            }
        }
      }

      lazy val pipelinable: Parser[Ast.Node] = rule(%% ~ castable ~ (CL(BAR) >> ident).? ^^ {
        case location ~ self ~ None =>
          self
        case location ~ self ~ Some(name) =>
          FunctionCall(location, name, List(self))
      })

      lazy val castable: Parser[Ast.Node] = rule(primary ~ ((%% << CL(COLONGT)) ~ CL(castType)).? ^^ {
        case target ~ Some((location ~ castType)) => Casting(location, target, castType)
        case target ~ None => target
      })

      //primary ::= selector | booleanLiteral | placeholder | realLiteral | integerLiteral | newRecord | newObject | functionLiteral | listLiteral | dictionaryLiteral | setLiteral | stringLiteral | newObject | functionLiteral | "(" expression ")" | "{" lines "}" | ident
      lazy val primary: Parser[Ast.Node] = rule {
        (
          selector
            | (%% ~< VISUALIZE ~< CL(LBRACE)) ~ expression ~< CL(RBRACE) ^^ { case location ~ expression => Show(location, expression) }
            | booleanLiteral
            | placeholder
            | realLiteral
            | integerLiteral
            | newRecord
            | newObject
            | functionLiteral
            | listLiteral
            | dictionaryLiteral
            | setLiteral
            | stringLiteral
            | (CL(LPAREN) >> expression << RPAREN)
            | (CL(LBRACE) >> lines << RBRACE)
            | ident
        )
      }

      //integerLiteral ::= ["1"-"9"] {"0"-"9"}
      lazy val integerLiteral: Parser[Ast.Node] = (%% ~ """[1-9１-９][0-9０-９]*|0|０""".r ~ ("BY" ^^ { _ => ByteSuffix }).?  ^^ {
        case location ~ value ~ None => IntNode(location, normalize(value).toLong.toInt)
        case location ~ value ~ Some(ByteSuffix) => ByteNode(location, normalize(value).toByte)
      }) << SPACING_WITHOUT_LF

      lazy val realLiteral: Parser[Ast.Node] = (%% ~ "([1-9０-９][0-9０-９]*|0|０)\\.[0-9０-９]*".r  ^^ {
        case location ~ value => RealNode(location, BigDecimal(normalize(value)))
      }) << SPACING_WITHOUT_LF

      lazy val booleanLiteral: Parser[Ast.Node] = %% ~ (TRUE ^^ { _ => true } | FALSE ^^ { _ => false }) ^^ {
        case location ~ true => BooleanNode(location, true)
        case location ~ false => BooleanNode(location, false)
      }

      //stringLiteral ::= "\"" ((?!")(\[rntfb"'\\]|[^\\]))* "\""
      lazy val stringLiteral: Parser[Ast.Node] =
        ("\"" >>
          (%% ~ """((?!("|#\{))(\\[rntfb"'\\]|[^\\]))+""".r ^^ { case location ~ in =>
            StringNode(location, unescape(in))
          } | "#{" >> expression << "}"
            ).*
          << "\"" ^^ { values =>
          values.foldLeft(StringNode(NoLocation, ""): Ast.Node) { (node, content) => BinaryExpression(content.location, Operator.ADD, node, content) }
        }) << SPACING_WITHOUT_LF

      lazy val listLiteral: Parser[Ast.Node] = rule(%% ~ (CL(LIST_BEGIN) >> commit((CL(expression).repeat0By(SEPARATOR) << SEPARATOR.?) << RPAREN)) ^^ {
        case location ~ contents => ListLiteral(location, contents)
      })

      lazy val setLiteral:  Parser[Ast.Node] = rule(%% ~ (CL(SET_BEGIN) >> commit((CL(expression).repeat0By(SEPARATOR) << SEPARATOR.?) << RPAREN)) ^^ {
        case location ~ contents => SetLiteral(location, contents)
      })

      lazy val dictionaryLiteral: Parser[Ast.Node] = rule(%% ~ (CL(DICTIONARY_BEGIN) >> commit((CL(expression ~ DICTIONARY_SEPARATOR ~ expression).repeat0By(SEPARATOR) << SEPARATOR.?) << RPAREN)) ^^ {
        case location ~ contents => DictionaryLiteral(location, contents.map { case k ~ colon ~ v => (k, v) })
      })

      lazy val fqcn: Parser[String] = (ident ~ (CL(DOT) ~ ident).*) ^^ {
        case id ~ ids => ids.foldLeft(id.name) { case (a, d ~ e) => a + d + e.name }
      }

      def normalize(input: String): String = {
        // 全角数字を半角数字に対応させるマップ
        val mapping = Map(
          '０' -> '0',
          '１' -> '1',
          '２' -> '2',
          '３' -> '3',
          '４' -> '4',
          '５' -> '5',
          '６' -> '6',
          '７' -> '7',
          '８' -> '8',
          '９' -> '9'
        )

        // 文字列の各文字を調べて、全角数字なら対応する半角数字に変換
        new String(input.toCharArray.map { char =>
          mapping.getOrElse(char, char)
        })
      }

      lazy val component: Parser[String] = (
        """[A-Za-z_][A-Za-z_0-9]*""".r
      | """「([A-Za-z_]|\p{InCjkUnifiedIdeographs}|\p{InHiragana}|\p{InKatakana})(\w|[\uFF10-\uFF19]|\p{InCjkUnifiedIdeographs}|\p{InHiragana}|\p{InKatakana})*」""".r.map(s => s.substring(1, s.length - 1))
      | """(\p{InCjkUnifiedIdeographs}|\p{InHiragana}|\p{InKatakana})(\w|[\uFF10-\uFF19]|\p{InCjkUnifiedIdeographs}|\p{InHiragana}|\p{InKatakana})*""".r
      ).map{s => normalize(s)}


      lazy val placeholder: Parser[Placeholder] = ((%% ~ UNDERSCORE) ^^ { case location ~ _ => Placeholder(location) }) << SPACING_WITHOUT_LF

      lazy val ident: Parser[Id] = (%% ~ component.filter { n =>
        !KEYWORDS(n)
      } ^^ { case location ~ name => Id(location, name) }) << SPACING_WITHOUT_LF

      def selector: Parser[Selector] = rule(((%% ~ component ~ "#" ~ component).filter { case (_ ~ m ~ _ ~ n) =>
        (!KEYWORDS(m)) && (!KEYWORDS(n))
      } ^^ { case location ~ m ~ _ ~ n => Selector(location, m, n) }) << SPACING_WITHOUT_LF)

      lazy val qident: Parser[String] = (string("'") ~> component).filter { n =>
        !KEYWORDS(n)
      } << SPACING_WITHOUT_LF

      lazy val sident: Parser[String] = (component.filter { n =>
        !KEYWORDS(n)
      }) << SPACING_WITHOUT_LF

      lazy val operator: Parser[String] = ((component).filter { n =>
        !KEYWORDS(n)
      }) << SPACING_WITHOUT_LF

      lazy val assignment: Parser[Assignment] = rule(ident ~ CL(PLUS_ASSIGN | MINUS_ASSIGN | MULT_ASSIGN | DIV_ASSIGN | LARROW) ~ expression ^^ {
        case v ~ ("<-" | "←") ~ value => SimpleAssignment(v.location, v.name, value)
        case v ~ ("+<-" | "+←") ~ value => PlusAssignment(v.location, v.name, value)
        case v ~ ("-<-" | "-←") ~ value => MinusAssignment(v.location, v.name, value)
        case v ~ ("*<-" | "*←") ~ value => MultiplicationAssignment(v.location, v.name, value)
        case v ~ ("/<-" | "/←") ~ value => DivisionAssignment(v.location, v.name, value)
        case _ ~ op ~ _ => sys.error(s"unknown assignment operator ${op}")
      })

      lazy val jpAssignment: Parser[Assignment] = rule(
        ident ~ CL(TO) ~ expression ~ CL(INCREMENT_BY | DECREMENT_BY) ^^ {
        case v ~ "を" ~ value ~ "増やす" => PlusAssignment(v.location, v.name, value)
        case v ~ "を" ~ value ~ "減らす" => MinusAssignment(v.location, v.name, value)
        case _ ~ op ~ _ => sys.error(s"unknown assignment operator ${op}")
      })

      // valDeclaration ::= "変数" ident "は" expression
      lazy val valDeclaration: Parser[ValDeclaration] = rule((%% ~ CL(VARIABLE ^^ { _ => false })) ~ commit(ident ~ (typeAnnotation.? << CL(JP_HA)) ~ expression) ^^ {
        case location ~ immutable ~ (valName ~ optionalType ~ value) => ValDeclaration(location, valName.name, optionalType, value, immutable)
      })

      // parenthesizedParameter ::= "(" [param {"," param}] ")"
      lazy val parenthesizedParameter: Parser[List[Ast.Node]] = rule {
        CL(LPAREN) >> CL(expression).repeat0By(CL(COMMA)) << (SPACING << RPAREN) ^^ {
          case xs => xs
        }
      }

      // blockFunctionParameter ::= "{" [param {"," param}] "=>" expression "}"
      lazy val blockFunctionParameter: Parser[List[Ast.Node]] = rule {
        (%% << CL(LBRACE)) ~ (ident ~ typeAnnotation.?).repeat0By(CL(COMMA)) ~ (typeAnnotation.? << CL(ARROW1)) ~ (expression << RBRACE) ^^ {
          case location ~ params ~ optionalType ~ body =>
            List(
              Lambda(
                location,
                params.map {
                  case name ~ Some(type_) => FormalParameterOptional(name.name, Some(type_))
                  case name ~ None => FormalParameterOptional(name.name, None)
                },
                optionalType,
                body
              )
            )
        }
      }

      // functionLiteral ::= "(" [param {"," param}] ")" "=>" expression
      lazy val functionLiteral: Parser[Ast.Node] = rule(%% ~ (CL(LPAREN) >> (ident ~ typeAnnotation.?).repeat0By(CL(COMMA)) << CL(RPAREN)).? ~ (typeAnnotation.? << CL(ARROW1)) ~ expression ^^ {
        case location ~ Some(params) ~ optionalType ~ body =>
          Lambda(
            location,
            params.map {
              case name ~ Some(type_) => FormalParameterOptional(name.name, Some(type_))
              case name ~ None => FormalParameterOptional(name.name, None)
            },
            optionalType,
            body
          )
        case location ~ None ~ optionalType ~ body => Lambda(location, List(), optionalType, body)
      })

      // newObject ::= "new" fqcn "(" [param {"," param} ")"
      lazy val newObject: Parser[Ast.Node] = rule((%% << CL(NEW)) ~ commit(fqcn ~ (CL(LPAREN) >> expression.repeat0By(CL(COMMA)) << RPAREN).?) ^^ {
        case location ~ (className ~ Some(params)) => ObjectNew(location, className, params)
        case location ~ (className ~ None) => ObjectNew(location, className, List())
      })

      // newRecord ::= "@" sident "(" [param {"," param} ")"
      lazy val newRecord: Parser[Ast.Node] = rule((%% << CL(ATMARK)) ~ commit(sident ~ (CL(LPAREN) >> expression.repeat0By(CL(COMMA)) << RPAREN).?) ^^ {
        case location ~ (recordName ~ Some(params)) => RecordNew(location, recordName, params)
        case location ~ (recordName ~ None) => RecordNew(location, recordName, List())
      })

      // methodDefinition ::= "def" ident  ["(" [param {"," param}] ")"] "=" expression
      lazy val methodDefinition: Parser[MethodDefinition] = rule {
        (%% << CL(DEF)) ~ commit(ident ~ (CL(LPAREN) >> (ident ~ typeAnnotation.?).repeat0By(CL(COMMA)) << CL(RPAREN)).? ~ (typeAnnotation.? << CL(JP_HA)) ~ expression) ^^ {
          case location ~ (functionName ~ params ~ optionalType ~ body) =>
            val ps = params match {
              case Some(xs) =>
                xs.map {
                  case name ~ Some(annotation) => FormalParameterOptional(name.name, Some(annotation))
                  case name ~ None => FormalParameterOptional(name.name, None)
                }
              case None => Nil
            }
            MethodDefinition(
              location,
              functionName.name,
              Lambda(body.location, ps, optionalType, body)
            )
        }
      }

      // functionDefinition ::= "def" ident  ["(" [param {"," param}] ")"] "=" expression
      lazy val functionDefinition: Parser[FunctionDefinition] = rule {
        (%% << CL(DEF)) ~ commit(ident ~ (CL(LPAREN) >> (ident ~ typeAnnotation.?).repeat0By(CL(COMMA)) << CL(RPAREN)).? ~ (typeAnnotation.? << CL(JP_HA)) ~ expression) ^^ {
          case location ~ (functionName ~ params ~ optionalType ~ body) =>
            val ps = params match {
              case Some(xs) =>
                xs.map {
                  case name ~ Some(annotation) => FormalParameterOptional(name.name, Some(annotation))
                  case name ~ None => FormalParameterOptional(name.name, None)
                }
              case None => Nil
            }
            FunctionDefinition(
              location,
              functionName.name,
              Lambda(body.location, ps, optionalType, body),
            )
        }
      }
    }
  }

  import Core._

  def parseExpression(input: String): Ast.Node = {
    parse(Klassic.root, input) match {
      case Result.Success(program) => program.block
      case Result.Failure(location, message) => throw new InterpreterException(Some(SourceLocation(location.line, location.column)), s"${location}:${message}")
    }
  }

  def parseAll(input: String): Program = {
    parse(Klassic.root, input) match {
      case Result.Success(program) => program
      case Result.Failure(location, message) => throw new InterpreterException(Some(SourceLocation(location.line, location.column)), s"${location}:${message}")
    }
  }

  override final val name: String = "Parser"

  override final def process(input: String, session: InteractiveSession): Program = parseAll(input)
}
