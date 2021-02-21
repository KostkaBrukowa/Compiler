package org.example.compiler.parser.productions

import org.example.compiler.lexer.token.TerminalType
import org.example.compiler.parser.BufferedLexer
import org.example.compiler.parser.Lexer
import org.example.compiler.parser.productions.parsers.IfStatementParser
import org.example.compiler.parser.utils.UnexpectedToken
import spock.lang.Specification
import spock.lang.Unroll

import static org.example.compiler.lexer.LazyLexerTest.buildLazyLexer
import static org.example.compiler.utils.ExpressionUtils.cond
import static org.example.compiler.utils.ExpressionUtils.expr

class IfStatementParserTest extends Specification {
    Lexer lazyLexer

    @Unroll
    def "should return correct IfStatement from string '#inputString'"() {
        given:
        lazyLexer = buildLazyLexer(inputString)

        and:
        def bufferedLexer = new BufferedLexer(lazyLexer)

        when:
        def IfStatement = IfStatementParser.INSTANCE.parse(bufferedLexer)

        then:
        with(IfStatement) {
            condition == expectedCondition
            statement == expectedStatementBlock
            elseStatement == expectedElseBlock
        }

        where:
        expectedCondition                       | expectedStatementBlock                                                      | expectedElseBlock                                                                      | inputString
        cond(false, new IdReference("x", null)) | new StatementBlock([new ReturnStatement(expr(new IdReference("x", null)))]) | null                                                                                   | 'if(x) { return x; }'
        cond(false, new IdReference("x", null)) | new StatementBlock([new ReturnStatement(expr(new IdReference("x", null)))]) | new StatementBlock([])                                                                 | 'if(x) { return x; } else { }'
        cond(false, new IdReference("x", null)) | new StatementBlock([new ReturnStatement(expr(new IdReference("x", null)))]) | new IfStatement(cond(false, new IdReference("y", null)), new StatementBlock([]), null) | 'if(x) { return x; } else if(y) { }'
    }

    @Unroll
    def "should return unexpected token when input is incorrect for string '#inputString'"() {
        given:
        lazyLexer = buildLazyLexer(inputString)

        and:
        def bufferedLexer = new BufferedLexer(lazyLexer)

        when:
        IfStatementParser.INSTANCE.parse(bufferedLexer)

        then:
        def exception = thrown(UnexpectedToken)
        exception.message.contains(expectedToken.toString())

        where:
        expectedToken     | inputString
        TerminalType.ELSE | "else(x) { return x; } if { }"
    }
}


