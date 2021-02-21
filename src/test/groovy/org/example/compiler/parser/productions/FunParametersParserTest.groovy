package org.example.compiler.parser.productions

import org.example.compiler.lexer.token.IdToken
import org.example.compiler.lexer.token.TerminalType
import org.example.compiler.parser.BufferedLexer
import org.example.compiler.parser.Lexer
import org.example.compiler.parser.productions.parsers.FunParametersParser
import org.example.compiler.parser.utils.UnexpectedToken
import spock.lang.Specification
import spock.lang.Unroll

import static org.example.compiler.lexer.LazyLexerTest.buildLazyLexer

class FunParametersParserTest extends Specification {
    Lexer lazyLexer

    @Unroll
    def "should return correct fun parameters '#inputString'"() {
        given:
        lazyLexer = buildLazyLexer(inputString)

        and:
        def bufferedLexer = new BufferedLexer(lazyLexer)

        when:
        def funParameters = FunParametersParser.INSTANCE.parse(bufferedLexer)

        then:
        with(funParameters) {
            arguments == expectedArguments
            returnType == expectedReturnType
        }

        where:
        expectedArguments                                                 | expectedReturnType                 | inputString
        [new ArgumentWithType("id", new BasicType(TerminalType.STRING))]  | new BasicType(TerminalType.STRING) | "(id: String): String"
        []                                                                | new BasicType(TerminalType.STRING) | "(): String"
        [new ArgumentWithType("id", new BasicType(TerminalType.STRING)),
         new ArgumentWithType("foo", new BasicType(TerminalType.NUMBER))] | new BasicType(TerminalType.LIST)   | "(id: String, foo: Number): List"
    }

    @Unroll
    def "should throw syntax error when input is incorrect '#inputString'"() {
        given:
        lazyLexer = buildLazyLexer(inputString)

        and:
        def bufferedLexer = new BufferedLexer(lazyLexer)

        when:
        FunParametersParser.INSTANCE.parse(bufferedLexer)

        then:
        def exception = thrown(UnexpectedToken)
        exception.message.contains(expectedTokenType.toString())

        where:
        expectedTokenType      | inputString
        new IdToken("id")      | "id: String): String"
        TerminalType.SEMICOLON | "(id: String);"
        TerminalType.COMMA     | "(id: String,);"
    }
}
