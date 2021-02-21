package org.example.compiler.parser.productions

import org.example.compiler.lexer.token.TerminalType
import org.example.compiler.parser.BufferedLexer
import org.example.compiler.parser.Lexer
import org.example.compiler.parser.productions.parsers.StatementBlockParser
import spock.lang.Specification
import spock.lang.Unroll

import static org.example.compiler.lexer.LazyLexerTest.buildLazyLexer
import static org.example.compiler.utils.ExpressionUtils.expr

class StatementBlockParserTest extends Specification {
    Lexer lazyLexer

    @Unroll
    def "should return correct StatementBlock from string '#inputString'"() {
        given:
        lazyLexer = buildLazyLexer(inputString)

        and:
        def bufferedLexer = new BufferedLexer(lazyLexer)

        when:
        def StatementBlock = StatementBlockParser.INSTANCE.parse(bufferedLexer)

        then:
        with(StatementBlock) {
            statements == expectedStatements
        }

        where:
        expectedStatements                                                                                                                                                                                                                                           | inputString
        [new ReturnStatement(expr(new IdReference("x", null)))]                                                                                                                                                                                                      | '{ return x; }'
        [new InitStatement("x", new ArgumentWithType("x", new BasicType(TerminalType.NUMBER)), expr(new NumberLiteral(1))), new StatementBlock([new InitStatement("y", new ArgumentWithType("y", new BasicType(TerminalType.NUMBER)), expr(new NumberLiteral(2)))])] | '{ val x: Number = 1; { val y: Number = 2; } }'
        [new InitStatement("x", new ArgumentWithType("x", new BasicType(TerminalType.NUMBER)), expr(new NumberLiteral(1))), new InitStatement("y", new ArgumentWithType("y", new BasicType(TerminalType.NUMBER)), expr(new NumberLiteral(2)))]                       | '{ val x: Number = 1; val y: Number = 2; }'
    }
}

