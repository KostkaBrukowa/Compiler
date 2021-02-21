package org.example.compiler.parser.productions


import org.example.compiler.lexer.token.TerminalType
import org.example.compiler.lexer.token.TokenType
import org.example.compiler.parser.BufferedLexer
import org.example.compiler.parser.Lexer
import org.example.compiler.parser.productions.parsers.BasicTypeParser
import org.example.compiler.parser.utils.UnexpectedToken
import spock.lang.Specification
import spock.lang.Unroll

import static org.example.compiler.utils.TokenUtils.t

class BasicTypeParserTest extends Specification {
    Lexer lazyLexer = Mock()

    @Unroll
    def "should return correct fun parameters #inputTokens"() {
        given:
        lazyLexer.getNextTokenMeta() >>> inputTokens

        and:
        def bufferedLexer = new BufferedLexer(lazyLexer)

        when:
        def basicType = BasicTypeParser.INSTANCE.parse(bufferedLexer)

        then:
        basicType.type.class.toString() == expectedType

        where:
        expectedType                  | inputTokens
        TerminalType.class.toString() | [t(TerminalType.STRING)]
        TerminalType.class.toString() | [t(TerminalType.BOOLEAN)]
        TerminalType.class.toString() | [t(TerminalType.LIST)]
        TerminalType.class.toString() | [t(TerminalType.NUMBER)]
    }

    @Unroll
    def "should return unexpected token when input is incorrect: '#inputTokens'"() {
        given:
        lazyLexer.getNextTokenMeta() >>> inputTokens

        and:
        def bufferedLexer = new BufferedLexer(lazyLexer)

        when:
        BasicTypeParser.INSTANCE.parse(bufferedLexer)

        then:
        thrown(UnexpectedToken)

        where:
        inputTokens << [[t(TerminalType.OPEN_PAREN)],
                        [t(TerminalType.FUN)],
                        [t(TerminalType.FUNCTION)],
                        [t(TokenType.ID, "id")],
                        [t(TokenType.NUMBER, "1.11")]
        ]
    }
}
