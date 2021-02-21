package org.example.compiler.parser.productions

import org.example.compiler.lexer.token.TerminalType
import org.example.compiler.parser.BufferedLexer
import org.example.compiler.parser.Lexer
import org.example.compiler.parser.productions.parsers.EqualityConditionParser
import org.example.compiler.parser.utils.UnexpectedToken
import spock.lang.Specification
import spock.lang.Unroll

import static org.example.compiler.lexer.LazyLexerTest.buildLazyLexer

class EqualityConditionParserTest extends Specification {
    Lexer lazyLexer

    @Unroll
    def "should return correct EqualityCondition from string '#inputString'"() {
        given:
        lazyLexer = buildLazyLexer(inputString)

        and:
        def bufferedLexer = new BufferedLexer(lazyLexer)

        when:
        def EqualityCondition = EqualityConditionParser.INSTANCE.parse(bufferedLexer)

        then:
        with(EqualityCondition) {
            leftHandSideCondition == expectedLeftHandSide
            operator == expectedOperator
            rightHandSideCondition == expectedRightHandSide
        }

        where:
        expectedLeftHandSide                 | expectedOperator             | expectedRightHandSide | inputString
        relationalCond("a")                  | TerminalType.EQUALS_EQUALS   | relationalCond("b")   | 'a == b'
        relationalCond("a")                  | TerminalType.DOES_NOT_EQUALS | relationalCond("b")   | 'a != b'
        relationalCond("a", new FunCall([])) | TerminalType.DOES_NOT_EQUALS | relationalCond("b")   | 'a() != b'
    }

    @Unroll
    def "should return unexpected token when input is incorrect for string '#inputString'"() {
        given:
        lazyLexer = buildLazyLexer(inputString)

        and:
        def bufferedLexer = new BufferedLexer(lazyLexer)

        when:
        EqualityConditionParser.INSTANCE.parse(bufferedLexer)

        then:
        def exception = thrown(UnexpectedToken)
        exception.message.contains(expectedToken.toString())

        where:
        expectedToken            | inputString
        TerminalType.LAMBDA | "lambda (): String"
        TerminalType.CLOSE_PAREN | "a ==  )"
    }

    static def relationalCond(String id, IdReferenceArgument argument = null) {
        return new RelationalCondition(new PrimaryCondition(false, new IdReference(id, argument)), null, null)
    }
}


