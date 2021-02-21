package org.example.compiler.parser.productions.subproductions

import org.example.compiler.lexer.token.TerminalType
import org.example.compiler.parser.BufferedLexer
import org.example.compiler.parser.Lexer
import org.example.compiler.parser.productions.BasicType
import org.example.compiler.parser.productions.FunctionType
import org.example.compiler.parser.utils.UnexpectedToken
import spock.lang.Specification
import spock.lang.Unroll

import static org.example.compiler.lexer.LazyLexerTest.buildLazyLexer

class TypeObjectParserTest extends Specification {
    Lexer lazyLexer

    @Unroll
    def "should return correct fun parameters from string '#inputString'"() {
        given:
        lazyLexer = buildLazyLexer(inputString)

        and:
        def bufferedLexer = new BufferedLexer(lazyLexer)

        when:
        def typeObject = TypeObjectParser.INSTANCE.parse(bufferedLexer)

        then:
        typeObject.class.toString() == expectedClass.toString()

        where:
        expectedClass      | inputString
        FunctionType.class | "Function<Arg<String>, Returns<String>>"
        BasicType.class    | "String"
    }

    @Unroll
    def "should return unexpected token when input is incorrect for string '#inputString'"() {
        given:
        lazyLexer = buildLazyLexer(inputString)

        and:
        def bufferedLexer = new BufferedLexer(lazyLexer)

        when:
        TypeObjectParser.INSTANCE.parse(bufferedLexer)

        then:
        def exception = thrown(UnexpectedToken)
        exception.message.contains(expectedToken.toString())

        where:
        expectedToken        | inputString
        "foo"                | "foo"
        TerminalType.RETURNS | "Returns"
    }
}
