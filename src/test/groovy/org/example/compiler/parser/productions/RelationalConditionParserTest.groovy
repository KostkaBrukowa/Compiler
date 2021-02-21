package org.example.compiler.parser.productions

import org.example.compiler.lexer.token.TerminalType
import org.example.compiler.parser.BufferedLexer
import org.example.compiler.parser.Lexer
import org.example.compiler.parser.productions.parsers.RelationalConditionParser
import org.example.compiler.parser.utils.UnexpectedToken
import spock.lang.Specification
import spock.lang.Unroll

import static org.example.compiler.lexer.LazyLexerTest.buildLazyLexer
import static org.example.compiler.utils.ExpressionUtils.cond

class RelationalConditionParserTest extends Specification {
    Lexer lazyLexer

    @Unroll
    def "should return correct RelationalCondition from string '#inputString'"() {
        given:
        lazyLexer = buildLazyLexer(inputString)

        and:
        def bufferedLexer = new BufferedLexer(lazyLexer)

        when:
        def RelationalCondition = RelationalConditionParser.INSTANCE.parse(bufferedLexer)

        then:
        with(RelationalCondition) {
            leftHandSideCondition == expectedLeftHandSide
            operator == expectedOperator
            rightHandSideCondition == expectedRightHandSide
        }

        where:
        expectedLeftHandSide                                               | expectedOperator                 | expectedRightHandSide                                                                          | inputString
        new PrimaryCondition(false, new IdReference("a", null))            | TerminalType.GREATER_THAN        | new PrimaryCondition(false, new IdReference("b", null))                                        | 'a > b'
        new PrimaryCondition(false, new IdReference("a", null))            | TerminalType.GREATER_EQUALS_THAN | new PrimaryCondition(false, new IdReference("b", null))                                        | 'a >= b'
        new PrimaryCondition(false, new IdReference("a", new FunCall([]))) | TerminalType.GREATER_EQUALS_THAN | new PrimaryCondition(false, new IdReference("b", null))                                        | 'a() >= b'
        new PrimaryCondition(false, new IdReference("a", null))            | TerminalType.GREATER_THAN        | new PrimaryCondition(false, new ParenthesisCondition(cond(false, new IdReference("b", null)))) | 'a > (b)'
    }

    @Unroll
    def "should return unexpected token when input is incorrect for string '#inputString'"() {
        given:
        lazyLexer = buildLazyLexer(inputString)

        and:
        def bufferedLexer = new BufferedLexer(lazyLexer)

        when:
        RelationalConditionParser.INSTANCE.parse(bufferedLexer)

        then:
        def exception = thrown(UnexpectedToken)
        exception.message.contains(expectedToken.toString())

        where:
        expectedToken             | inputString
        TerminalType.GREATER_THAN | "a>>>b"
        TerminalType.LAMBDA | "lambda (): String"
        TerminalType.CLOSE_PAREN  | "a > )"
    }
}


