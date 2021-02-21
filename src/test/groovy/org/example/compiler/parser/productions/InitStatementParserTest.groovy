package org.example.compiler.parser.productions

import org.example.compiler.lexer.token.TerminalType
import org.example.compiler.parser.BufferedLexer
import org.example.compiler.parser.Lexer
import org.example.compiler.parser.productions.parsers.InitStatementParser
import org.example.compiler.parser.utils.NoMoreTokensDuringParseException
import spock.lang.Specification
import spock.lang.Unroll

import static org.example.compiler.lexer.LazyLexerTest.buildLazyLexer
import static org.example.compiler.utils.ExpressionUtils.expr

class InitStatementParserTest extends Specification {
    Lexer lazyLexer

    @Unroll
    def "should return correct InitStatement from string '#inputString'"() {
        given:
        lazyLexer = buildLazyLexer(inputString)

        and:
        def bufferedLexer = new BufferedLexer(lazyLexer)

        when:
        def InitStatement = InitStatementParser.INSTANCE.parse(bufferedLexer)

        then:
        with(InitStatement) {
            argumentWithType == expectedArgWithType
            assignable == expectedAssignable
        }

        where:
        expectedArgWithType                                   | expectedAssignable               | inputString
        new ArgumentWithType("x", new BasicType(TerminalType.STRING)) | expr(new StringLiteral("jarek")) | 'val x: String = "jarek";'
    }

    @Unroll
    def "should return unexpected token when input is incorrect for string '#inputString'"() {
        given:
        lazyLexer = buildLazyLexer(inputString)

        and:
        def bufferedLexer = new BufferedLexer(lazyLexer)

        when:
        InitStatementParser.INSTANCE.parse(bufferedLexer)

        then:
        thrown(NoMoreTokensDuringParseException)

        where:
        inputString << ['val x: String = "jarek"']
    }
}


