package org.example.compiler.parser.productions

import org.example.compiler.lexer.token.TerminalType
import org.example.compiler.parser.BufferedLexer
import org.example.compiler.parser.Lexer
import org.example.compiler.parser.productions.parsers.ReturnStatementParser
import org.example.compiler.parser.utils.NoMoreTokensDuringParseException
import spock.lang.Specification
import spock.lang.Unroll

import static org.example.compiler.lexer.LazyLexerTest.buildLazyLexer
import static org.example.compiler.utils.ExpressionUtils.expr

class ReturnStatementParserTest extends Specification {
    Lexer lazyLexer

    @Unroll
    def "should return correct ReturnStatement from string '#inputString'"() {
        given:
        lazyLexer = buildLazyLexer(inputString)

        and:
        def bufferedLexer = new BufferedLexer(lazyLexer)

        when:
        def ReturnStatement = ReturnStatementParser.INSTANCE.parse(bufferedLexer)

        then:
        with(ReturnStatement) {
            assignable == expectedAssignable
        }

        where:
        expectedAssignable                                                                                       | inputString
        expr(new IdReference("x", null))                                                                         | 'return x;'
        new AnonymousFunction(new FunParameters([], new BasicType(TerminalType.STRING)), new StatementBlock([])) | 'return (): String => {} ;'
    }

    @Unroll
    def "should return unexpected token when input is incorrect for string '#inputString'"() {
        given:
        lazyLexer = buildLazyLexer(inputString)

        and:
        def bufferedLexer = new BufferedLexer(lazyLexer)

        when:
        ReturnStatementParser.INSTANCE.parse(bufferedLexer)

        then:
        thrown(NoMoreTokensDuringParseException)

        where:
        inputString << ['return x']

    }
}


