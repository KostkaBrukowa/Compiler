package org.example.compiler.parser.productions

import org.example.compiler.lexer.token.IdToken
import org.example.compiler.lexer.token.TerminalType
import org.example.compiler.parser.BufferedLexer
import org.example.compiler.parser.Lexer
import org.example.compiler.parser.productions.parsers.FunctionDefParser
import org.example.compiler.parser.utils.NoMoreTokensDuringParseException
import org.example.compiler.parser.utils.UnexpectedToken
import spock.lang.Specification
import spock.lang.Unroll

import static org.example.compiler.lexer.LazyLexerTest.buildLazyLexer
import static org.example.compiler.utils.ExpressionUtils.expr

class FunctionDefParserTest extends Specification {
    Lexer lazyLexer

    @Unroll
    def "should return correct FunctionDef from string '#inputString'"() {
        given:
        lazyLexer = buildLazyLexer(inputString)

        and:
        def bufferedLexer = new BufferedLexer(lazyLexer)

        when:
        def FunctionDef = FunctionDefParser.INSTANCE.parse(bufferedLexer)

        then:
        with(FunctionDef) {
            id == expectedId
            parameters == expectedParameters
            block == expectedStatement
        }

        where:
        expectedId | expectedParameters                                                                                                                                                                        | expectedStatement                                                         | inputString
        "foo"      | new FunParameters([], new BasicType(TerminalType.STRING))                                                                                                                                 | new StatementBlock([])                                                    | 'fun foo(): String {}'
        "foo"      | new FunParameters([], new BasicType(TerminalType.STRING))                                                                                                                                 | new StatementBlock([new ReturnStatement(expr(new StringLiteral("bar")))]) | 'fun foo(): String { return "bar"; }'
        "foo"      | new FunParameters([new ArgumentWithType("bar", new BasicType(TerminalType.STRING))], new BasicType(TerminalType.STRING))                                                                  | new StatementBlock([])                                                    | 'fun foo(bar: String): String {}'
        "foo"      | new FunParameters([new ArgumentWithType("bar", new BasicType(TerminalType.STRING)), new ArgumentWithType("baz", new BasicType(TerminalType.NUMBER))], new BasicType(TerminalType.NUMBER)) | new StatementBlock([])                                                    | 'fun foo(bar: String, baz: Number): Number {}'
    }

    @Unroll
    def "should return unexpected token when input is incorrect for string '#inputString'"() {
        given:
        lazyLexer = buildLazyLexer(inputString)

        and:
        def bufferedLexer = new BufferedLexer(lazyLexer)

        when:
        FunctionDefParser.INSTANCE.parse(bufferedLexer)

        then:
        def exception = thrown(UnexpectedToken)
        exception.message.contains(expectedToken.toString())

        where:
        expectedToken             | inputString
        new IdToken("foo")        | "foo(bar: String, baz: Number): Number {}"
        TerminalType.OPEN_PAREN   | "fun (bar: String, baz: Number): Number {}"
        TerminalType.COMMA        | "fun foo(bar: String,): Number {}"
        TerminalType.OPEN_BRACKET | "fun foo(bar: String) {}"
        TerminalType.OPEN_PAREN   | "fun foo {}"
    }

    def "should return no more tokens exception when input string was #inputString"() {
        given:
        lazyLexer = buildLazyLexer(inputString)

        and:
        def bufferedLexer = new BufferedLexer(lazyLexer)

        when:
        FunctionDefParser.INSTANCE.parse(bufferedLexer)

        then:
        thrown(NoMoreTokensDuringParseException)

        where:
        inputString << ["fun foo(): String { "]
    }
}


