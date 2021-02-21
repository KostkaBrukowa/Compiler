package org.example.compiler.parser.productions

import org.example.compiler.lexer.token.TerminalType
import org.example.compiler.parser.BufferedLexer
import org.example.compiler.parser.Lexer
import org.example.compiler.parser.productions.parsers.ParenthesisConditionParser
import org.example.compiler.parser.utils.UnexpectedToken
import spock.lang.Specification
import spock.lang.Unroll

import static org.example.compiler.lexer.LazyLexerTest.buildLazyLexer
import static org.example.compiler.utils.ExpressionUtils.cond

class ParenthesisConditionParserTest extends Specification {
    Lexer lazyLexer

    @Unroll
    def "should return correct ParenthesisCondition from string '#inputString'"() {
        given:
        lazyLexer = buildLazyLexer(inputString)

        and:
        def bufferedLexer = new BufferedLexer(lazyLexer)

        when:
        def ParenthesisCondition = ParenthesisConditionParser.INSTANCE.parse(bufferedLexer)

        then:
        with(ParenthesisCondition) {
            condition == expectedCondition
        }

        where:
        expectedCondition                                                                | inputString
        cond(false, new IdReference("foo", null))                                        | '(foo)'
        cond(true, new IdReference("foo", null))                                         | '(!foo)'
        cond(true, new IdReference("foo", new FunCall([])))                              | '(!foo())'
        cond(false, new ParenthesisCondition(cond(false, new IdReference("foo", null)))) | '((foo))'
        cond(true, new ParenthesisCondition(cond(false, new IdReference("foo", null))))  | '(!(foo))'
    }

    @Unroll
    def "should return unexpected token when input is incorrect for string '#inputString'"() {
        given:
        lazyLexer = buildLazyLexer(inputString)

        and:
        def bufferedLexer = new BufferedLexer(lazyLexer)

        when:
        ParenthesisConditionParser.INSTANCE.parse(bufferedLexer)

        then:
        def exception = thrown(UnexpectedToken)
        exception.message.contains(expectedToken.toString())

        where:
        expectedToken           | inputString
        TerminalType.OPEN_PAREN | "(())"
        TerminalType.OPEN_PAREN | "()foo)"
    }
}


