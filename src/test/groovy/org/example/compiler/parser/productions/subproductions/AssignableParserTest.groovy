package org.example.compiler.parser.productions.subproductions

import org.example.compiler.parser.BufferedLexer
import org.example.compiler.parser.Lexer
import org.example.compiler.parser.productions.AnonymousFunction
import org.example.compiler.parser.productions.Expression
import spock.lang.Specification
import spock.lang.Unroll

import static org.example.compiler.lexer.LazyLexerTest.buildLazyLexer

class AssignableParserTest extends Specification {
    Lexer lazyLexer

    @Unroll
    def "should return correct Assignable from string '#inputString'"() {
        given:
        lazyLexer = buildLazyLexer(inputString)

        and:
        def bufferedLexer = new BufferedLexer(lazyLexer)

        when:
        def PrimaryExpression = AssignableParser.INSTANCE.parse(bufferedLexer)

        then:
        PrimaryExpression.class.toString() == expectedType.toString()

        where:
        expectedType      | inputString
        Expression        | '1 + 2'
        AnonymousFunction | '(): String => {}'
        Expression        | '(x)'
    }
}


