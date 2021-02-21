package org.example.compiler.parser.productions

import org.example.compiler.parser.BufferedLexer
import org.example.compiler.parser.Lexer
import org.example.compiler.parser.productions.parsers.PrimaryExpressionParser
import spock.lang.Specification
import spock.lang.Unroll

import static org.example.compiler.lexer.LazyLexerTest.buildLazyLexer

class PrimaryExpressionParserTest extends Specification {
    Lexer lazyLexer

    @Unroll
    def "should return correct PrimaryExpression from string '#inputString'"() {
        given:
        lazyLexer = buildLazyLexer(inputString)

        and:
        def bufferedLexer = new BufferedLexer(lazyLexer)

        when:
        def PrimaryExpression = PrimaryExpressionParser.INSTANCE.parse(bufferedLexer)

        then:
        PrimaryExpression.class.toString() == expectedType.toString()

        where:
        expectedType          | inputString
        NumberLiteral         | '11'
        IdReference           | 'id1 id2'
        ListExpression        | '[].map(foo)'
        ListExpression        | 'list.map(foo)'
        ParenthesisExpression | '(11)'
    }
}


