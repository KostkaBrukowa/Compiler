package org.example.compiler.parser.productions

import org.example.compiler.parser.BufferedLexer
import org.example.compiler.parser.Lexer
import spock.lang.Specification
import spock.lang.Unroll

import static org.example.compiler.lexer.LazyLexerTest.buildLazyLexer

class LiteralParserTest extends Specification {
    Lexer lazyLexer

    @Unroll
    def "should return correct Literal from string '#inputString'"() {
        given:
        lazyLexer = buildLazyLexer(inputString)

        and:
        def bufferedLexer = new BufferedLexer(lazyLexer)

        when:
        def Literal = LiteralParser.INSTANCE.parse(bufferedLexer)

        then:
        Literal == expectedType

        where:
        expectedType               | inputString
        new StringLiteral("jarek") | '"jarek"'
        new NumberLiteral(11)      | '11'
    }
}


