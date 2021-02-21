package org.example.compiler.parser.productions

import org.example.compiler.lexer.token.NumberToken
import org.example.compiler.lexer.token.TerminalType
import org.example.compiler.parser.BufferedLexer
import org.example.compiler.parser.Lexer
import org.example.compiler.parser.productions.parsers.ListProductionParser
import org.example.compiler.parser.utils.UnexpectedToken
import spock.lang.Specification
import spock.lang.Unroll

import static org.example.compiler.lexer.LazyLexerTest.buildLazyLexer
import static org.example.compiler.utils.ExpressionUtils.expr

class ListProductionParserTest extends Specification {
    Lexer lazyLexer

    @Unroll
    def "should return correct ListLiteral from string '#inputString'"() {
        given:
        lazyLexer = buildLazyLexer(inputString)

        and:
        def bufferedLexer = new BufferedLexer(lazyLexer)

        when:
        def ListLiteral = ListProductionParser.INSTANCE.parse(bufferedLexer)

        then:
        with(ListLiteral) {
            elements == expectedElements
        }

        where:
        expectedElements                                                                                               | inputString
        [expr(new NumberLiteral(1)), expr(new NumberLiteral(2)), expr(new StringLiteral("3"))]                         | '[1, 2, "3"]'
        []                                                                                                             | '[]'
        [expr(new NumberLiteral(1)), expr(new IdReference("foo", new FunCall([])))]                                    | '[1, foo()]'
        [expr(new NumberLiteral(1)), expr(new IdReference("foo", new IndexingReference(expr(new NumberLiteral(12)))))] | '[1, foo[12]]'
        [expr(new ListExpression(new ListProduction([]), []))] | '[[]]'
    }

    @Unroll
    def "should return unexpected token when input is incorrect for string '#inputString'"() {
        given:
        lazyLexer = buildLazyLexer(inputString)

        and:
        def bufferedLexer = new BufferedLexer(lazyLexer)

        when:
        ListProductionParser.INSTANCE.parse(bufferedLexer)

        then:
        def exception = thrown(UnexpectedToken)
        exception.message.contains(expectedToken.toString())

        where:
        expectedToken                     | inputString
        TerminalType.COMMA                | "[1,2,]"
        TerminalType.COMMA                | "[,"
        TerminalType.CLOSE_SQUARE_BRACKET | "]["
        TerminalType.COMMA                | "[1,, 2]"
        new NumberToken(2) | "[1 2]"
    }
}
