package org.example.compiler.parser.productions

import org.example.compiler.lexer.token.TerminalType
import org.example.compiler.parser.BufferedLexer
import org.example.compiler.parser.Lexer
import org.example.compiler.parser.productions.parsers.ArgumentWithTypeParser
import org.example.compiler.parser.utils.UnexpectedToken
import spock.lang.Specification
import spock.lang.Unroll

import static org.example.compiler.lexer.LazyLexerTest.buildLazyLexer

class ArgumentWithTypeParserTest extends Specification {
    Lexer lazyLexer

    @Unroll
    def "should return correct argument with type from string '#inputString'"() {
        given:
        lazyLexer = buildLazyLexer(inputString)

        and:
        def bufferedLexer = new BufferedLexer(lazyLexer)

        when:
        def argumentWithType = ArgumentWithTypeParser.INSTANCE.parse(bufferedLexer)

        then:
        with(argumentWithType) {
            id == expectedId
            type == expectedType
        }

        where:
        expectedId | expectedType                                             | inputString
        "foo"      | new BasicType(TerminalType.STRING)                       | "foo: String"
        "bazzzz"   | new BasicType(TerminalType.NUMBER)                       | "bazzzz: Number"
        "x"        | new FunctionType([], new BasicType(TerminalType.STRING)) | "x : Function<Arg<>, Returns<String>>"
    }

    @Unroll
    def "should return unexpected token when input is incorrect for string '#inputString'"() {
        given:
        lazyLexer = buildLazyLexer(inputString)

        and:
        def bufferedLexer = new BufferedLexer(lazyLexer)

        when:
        ArgumentWithTypeParser.INSTANCE.parse(bufferedLexer)

        then:
        def exception = thrown(UnexpectedToken)
        exception.message.contains(expectedToken.toString())

        where:
        expectedToken         | inputString
        TerminalType.FUNCTION | "Function<Arg<>, Returns<String>>: "
        "foo"                 | "foo: foo"
        TerminalType.COLON    | "foo:: String"
        TerminalType.COLON    | ": String"
    }
}
