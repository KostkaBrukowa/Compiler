package org.example.compiler.parser.productions

import org.example.compiler.lexer.token.TerminalType
import org.example.compiler.parser.BufferedLexer
import org.example.compiler.parser.Lexer
import org.example.compiler.parser.utils.UnexpectedToken
import spock.lang.Specification
import spock.lang.Unroll

import static org.example.compiler.lexer.LazyLexerTest.buildLazyLexer

class NumberLiteralParserTest extends Specification {
    Lexer lazyLexer

    @Unroll
    def "should return correct NumberLiteral from string '#inputString'"() {
        given:
        lazyLexer = buildLazyLexer(inputString)

        and:
        def bufferedLexer = new BufferedLexer(lazyLexer)

        when:
        def NumberLiteral = NumberLiteralParser.INSTANCE.parse(bufferedLexer)

        then:
        with(NumberLiteral) {
            value == expectedValue
        }

        where:
        expectedValue | inputString
        11.1f         | "11.1"
        0.1f          | ".1"
    }

    @Unroll
    def "should return unexpected token when input is incorrect for string '#inputString'"() {
        given:
        lazyLexer = buildLazyLexer(inputString)

        and:
        def bufferedLexer = new BufferedLexer(lazyLexer)

        when:
        NumberLiteralParser.INSTANCE.parse(bufferedLexer)

        then:
        def exception = thrown(UnexpectedToken)
        exception.message.contains(expectedToken.toString())

        where:
        expectedToken         | inputString
        TerminalType.FUNCTION | "Function<Arg<>, Returns<String>>: "
    }
}
