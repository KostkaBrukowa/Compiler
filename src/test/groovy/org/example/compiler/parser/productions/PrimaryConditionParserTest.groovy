package org.example.compiler.parser.productions

import org.example.compiler.lexer.token.TerminalType
import org.example.compiler.parser.BufferedLexer
import org.example.compiler.parser.Lexer
import org.example.compiler.parser.productions.parsers.PrimaryConditionParser
import org.example.compiler.parser.utils.UnexpectedToken
import spock.lang.Specification
import spock.lang.Unroll

import static org.example.compiler.lexer.LazyLexerTest.buildLazyLexer
import static org.example.compiler.utils.ExpressionUtils.cond

class PrimaryConditionParserTest extends Specification {
    Lexer lazyLexer

    @Unroll
    def "should return correct PrimaryCondition from string '#inputString'"() {
        given:
        lazyLexer = buildLazyLexer(inputString)

        and:
        def bufferedLexer = new BufferedLexer(lazyLexer)

        when:
        def PrimaryCondition = PrimaryConditionParser.INSTANCE.parse(bufferedLexer)

        then:
        with(PrimaryCondition) {
            negated == expectedNegated
            conditionElement == expectedConditionElement
        }

        where:
        expectedNegated | expectedConditionElement                                            | inputString
        false           | new IdReference("foo", null)                                        | 'foo'
        false           | new NumberLiteral(11)                                               | '11'
        true            | new IdReference("foo", null)                                        | '!foo'
        false           | new IdReference("foo", new FunCall([]))                             | 'foo()'
        true            | new ParenthesisCondition(cond(false, new IdReference("foo", null))) | '!(foo)'
    }

    @Unroll
    def "should return unexpected token when input is incorrect for string '#inputString'"() {
        given:
        lazyLexer = buildLazyLexer(inputString)

        and:
        def bufferedLexer = new BufferedLexer(lazyLexer)

        when:
        PrimaryConditionParser.INSTANCE.parse(bufferedLexer)

        then:
        def exception = thrown(UnexpectedToken)
        exception.message.contains(expectedToken.toString())

        where:
        expectedToken       | inputString
        TerminalType.EQUALS | "=foo"
    }
}


