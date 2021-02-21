package org.example.compiler.parser.productions

import org.example.compiler.lexer.token.IdToken
import org.example.compiler.lexer.token.TerminalType
import org.example.compiler.parser.BufferedLexer
import org.example.compiler.parser.Lexer
import org.example.compiler.parser.productions.parsers.FunCallParser
import org.example.compiler.parser.utils.UnexpectedToken
import spock.lang.Specification
import spock.lang.Unroll

import static org.example.compiler.lexer.LazyLexerTest.buildLazyLexer
import static org.example.compiler.utils.ExpressionUtils.expr
import static org.example.compiler.utils.ExpressionUtils.listLiteral

class FunCallParserTest extends Specification {
    Lexer lazyLexer

    @Unroll
    def "should return correct FunCall from string '#inputString'"() {
        given:
        lazyLexer = buildLazyLexer(inputString)

        and:
        def bufferedLexer = new BufferedLexer(lazyLexer)

        when:
        def FunCall = FunCallParser.INSTANCE.parse(bufferedLexer)

        then:
        with(FunCall) {
            arguments == expectedArguments
        }

        where:
        expectedArguments                                                      | inputString
        [expr(listLiteral([new NumberLiteral(1), new NumberLiteral(2)]))] | '([1, 2])'
        [expr(new NumberLiteral(1)), expr(new NumberLiteral(2))]               | '(1, 2)'
        []                                                                     | '()'
        [expr(new StringLiteral("jarek")), expr(new StringLiteral("dominik"))] | '("jarek", "dominik")'
    }

    @Unroll
    def "should return unexpected token when input is incorrect for string '#inputString'"() {
        given:
        lazyLexer = buildLazyLexer(inputString)

        and:
        def bufferedLexer = new BufferedLexer(lazyLexer)

        when:
        FunCallParser.INSTANCE.parse(bufferedLexer)

        then:
        def exception = thrown(UnexpectedToken)
        exception.message.contains(expectedToken.toString())

        where:
        expectedToken      | inputString
        new IdToken("foo") | "foo(1,)"
        TerminalType.COMMA | "(1,)"
        TerminalType.COLON | "(1,2:"
        TerminalType.COMMA | "(,"
        TerminalType.COMMA | "(,)"
    }


}
