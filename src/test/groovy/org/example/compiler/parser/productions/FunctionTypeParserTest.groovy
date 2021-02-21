package org.example.compiler.parser.productions

import org.example.compiler.lexer.token.TerminalType
import org.example.compiler.parser.BufferedLexer
import org.example.compiler.parser.Lexer
import org.example.compiler.parser.productions.parsers.FunctionTypeParser
import org.example.compiler.parser.utils.UnexpectedToken
import spock.lang.Specification
import spock.lang.Unroll

import static org.example.compiler.lexer.LazyLexerTest.buildLazyLexer

class FunctionTypeParserTest extends Specification {
    Lexer lazyLexer

    @Unroll
    def "should return correct function types from string '#inputString'"() {
        given:
        lazyLexer = buildLazyLexer(inputString)

        and:
        def bufferedLexer = new BufferedLexer(lazyLexer)

        when:
        def functionType = FunctionTypeParser.INSTANCE.parse(bufferedLexer)

        then:
        with(functionType) {
            argumentsTypes == expectedArgumentTypes
            returnType == expectedReturnType
        }

        where:
        expectedArgumentTypes                                                    | expectedReturnType                                       | inputString
        [new BasicType(TerminalType.STRING)]                                     | new BasicType(TerminalType.STRING)                       | "Function<Arg<String>, Returns<String>>"
        [new BasicType(TerminalType.STRING), new BasicType(TerminalType.NUMBER)] | new BasicType(TerminalType.STRING)                       | "Function<Arg<String, Number>, Returns<String>>"
        []                                                                       | new BasicType(TerminalType.STRING)                       | "Function<Arg<>, Returns<String>>"
        [new FunctionType([], new BasicType(TerminalType.STRING))]               | new FunctionType([], new BasicType(TerminalType.STRING)) | "Function<Arg<Function<Arg<>, Returns<String>>>, Returns<Function<Arg<>, Returns<String>>>>"
    }

    @Unroll
    def "should return unexpected token when input is incorrect for string '#inputString'"() {
        given:
        lazyLexer = buildLazyLexer(inputString)

        and:
        def bufferedLexer = new BufferedLexer(lazyLexer)

        when:
        FunctionTypeParser.INSTANCE.parse(bufferedLexer)

        then:
        def exception = thrown(UnexpectedToken)
        exception.message.contains(expectedToken.toString())

        where:
        expectedToken          | inputString
        TerminalType.COMMA     | "Function<Arg<, Returns<String>>"
        TerminalType.COMMA     | "Function<Arg<String,>, Returns<String>>"
        TerminalType.LESS_THAN | "Function<Arg<>, <String>>"
    }
}
