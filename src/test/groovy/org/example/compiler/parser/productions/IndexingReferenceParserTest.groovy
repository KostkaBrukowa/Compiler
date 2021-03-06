package org.example.compiler.parser.productions

import org.example.compiler.lexer.token.IdToken
import org.example.compiler.lexer.token.NumberToken
import org.example.compiler.lexer.token.TerminalType
import org.example.compiler.parser.BufferedLexer
import org.example.compiler.parser.Lexer
import org.example.compiler.parser.productions.parsers.IndexingReferenceParser
import org.example.compiler.parser.utils.UnexpectedToken
import spock.lang.Specification
import spock.lang.Unroll

import static org.example.compiler.lexer.LazyLexerTest.buildLazyLexer
import static org.example.compiler.utils.ExpressionUtils.expr

class IndexingReferenceParserTest extends Specification {
    Lexer lazyLexer

    @Unroll
    def "should return correct VariableReference from string '#inputString'"() {
        given:
        lazyLexer = buildLazyLexer(inputString)

        and:
        def bufferedLexer = new BufferedLexer(lazyLexer)

        when:
        def IndexingReference = IndexingReferenceParser.INSTANCE.parse(bufferedLexer)

        then:
        with(IndexingReference) {
            expression == expectedExpression
        }

        where:
        expectedExpression                            | inputString
        expr(new NumberLiteral(11))                   | '[11]'
        expr(new IdReference("bar", new FunCall([]))) | '[bar()]'
        expr(new IdReference("bar", null))            | '[bar]'
    }

    @Unroll
    def "should return unexpected token when input is incorrect for string '#inputString'"() {
        given:
        lazyLexer = buildLazyLexer(inputString)

        and:
        def bufferedLexer = new BufferedLexer(lazyLexer)

        when:
        IndexingReferenceParser.INSTANCE.parse(bufferedLexer)

        then:
        def exception = thrown(UnexpectedToken)
        exception.message.contains(expectedToken.toString())

        where:
        expectedToken      | inputString
        new IdToken("foo") | "foo[1, 2]"
        TerminalType.COMMA | "[1, 2, "
        new NumberToken(2) | "[1 2]"
    }

    static def buildNumberExpression(Float number) {
        return new Expression([new MultiplicativeExpression([new NumberLiteral(number)], [])], [])
    }
}


