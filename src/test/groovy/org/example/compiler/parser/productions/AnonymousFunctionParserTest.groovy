package org.example.compiler.parser.productions

import org.example.compiler.lexer.token.IdToken
import org.example.compiler.lexer.token.TerminalType
import org.example.compiler.parser.BufferedLexer
import org.example.compiler.parser.Lexer
import org.example.compiler.parser.productions.parsers.AnonymousFunctionParser
import org.example.compiler.parser.utils.UnexpectedToken
import spock.lang.Specification
import spock.lang.Unroll

import static org.example.compiler.lexer.LazyLexerTest.buildLazyLexer
import static org.example.compiler.utils.ExpressionUtils.expr

class AnonymousFunctionParserTest extends Specification {
    Lexer lazyLexer

    @Unroll
    def "should return correct AnonymousFunction from string '#inputString'"() {
        given:
        lazyLexer = buildLazyLexer(inputString)

        and:
        def bufferedLexer = new BufferedLexer(lazyLexer)

        when:
        def AnonymousFunction = AnonymousFunctionParser.INSTANCE.parse(bufferedLexer)

        then:
        with(AnonymousFunction) {
            parameters == expectedParameters
            block == expectedBlock
        }

        where:
        expectedParameters                                                                                                                                                                         | expectedBlock                                                               | inputString
        new FunParameters([], new BasicType(TerminalType.STRING))                                                                                                                                  | new StatementBlock([new ReturnStatement(expr(new IdReference("x", null)))]) | '(): String => { return x; }'
        new FunParameters([new ArgumentWithType("foo", new BasicType(TerminalType.NUMBER))], new BasicType(TerminalType.STRING))                                                                   | new StatementBlock([])                                                      | '(foo: Number): String => { }'
        new FunParameters([new ArgumentWithType("foo", new BasicType(TerminalType.NUMBER)), new ArgumentWithType("bar", new BasicType(TerminalType.STRING)),], new BasicType(TerminalType.NUMBER)) | new StatementBlock([])                                                      | '(foo: Number, bar: String): Number => { }'
    }

    @Unroll
    def "should return unexpected token when input is incorrect for string '#inputString'"() {
        given:
        lazyLexer = buildLazyLexer(inputString)

        and:
        def bufferedLexer = new BufferedLexer(lazyLexer)

        when:
        AnonymousFunctionParser.INSTANCE.parse(bufferedLexer)

        then:
        def exception = thrown(UnexpectedToken)
        exception.message.contains(expectedToken.toString())

        where:
        expectedToken         | inputString
        new IdToken("lambda") | "lambda (): String => { return x; }"
        TerminalType.ARROW    | "() => { return x; }"
    }
}


