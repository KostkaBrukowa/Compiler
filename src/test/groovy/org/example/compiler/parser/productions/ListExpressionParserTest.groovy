package org.example.compiler.parser.productions

import org.example.compiler.parser.BufferedLexer
import org.example.compiler.parser.Lexer
import org.example.compiler.parser.productions.parsers.ListExpressionParser
import spock.lang.Specification
import spock.lang.Unroll

import static org.example.compiler.lexer.LazyLexerTest.buildLazyLexer

class ListExpressionParserTest extends Specification {
    Lexer lazyLexer

    @Unroll
    def "should return correct ListExpression from string '#inputString'"() {
        given:
        lazyLexer = buildLazyLexer(inputString)

        and:
        def bufferedLexer = new BufferedLexer(lazyLexer)

        when:
        def ListExpression = ListExpressionParser.INSTANCE.parse(bufferedLexer)

        then:
        with(ListExpression) {
            value == expectedListAsValue
        }

        where:
        expectedListAsValue                         | inputString
        new ListProduction([])                      | '[]^filter(foo)'
        new ListProduction([])                      | '[]^map(foo)'
        new ListProduction([])                      | '[]^concat(foo)'
        new IdReference("bar", null)                | 'bar^concat(foo)'
        new IdReference("newList", new FunCall([])) | 'newList()^concat(foo)'
    }
}


