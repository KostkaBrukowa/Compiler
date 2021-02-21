package org.example.compiler.parser.productions

import org.example.compiler.lexer.token.IdToken
import org.example.compiler.lexer.token.TerminalType
import org.example.compiler.parser.BufferedLexer
import org.example.compiler.parser.Lexer
import org.example.compiler.parser.productions.parsers.ListModifierParser
import org.example.compiler.parser.utils.UnexpectedToken
import spock.lang.Specification
import spock.lang.Unroll

import static org.example.compiler.lexer.LazyLexerTest.buildLazyLexer

class ListModifierParserTest extends Specification {
    Lexer lazyLexer

    @Unroll
    def "should return correct ListModifier from string '#inputString'"() {
        given:
        lazyLexer = buildLazyLexer(inputString)

        and:
        def bufferedLexer = new BufferedLexer(lazyLexer)

        when:
        def ListModifier = ListModifierParser.INSTANCE.parse(bufferedLexer)

        then:
        with(ListModifier) {
            modifier == expectedModifier
            callback == expectedCallback
        }

        where:
        expectedModifier    | expectedCallback                                                                                         | inputString
        TerminalType.FILTER | new IdReference("isEven", null)                                                                          | '.filter(isEven)'
        TerminalType.FILTER | new IdReference("isEven", new FunCall([]))                                                               | '.filter(isEven())'
        TerminalType.MAP    | new IdReference("isEven", new FunCall([]))                                                               | '.map(isEven())'
        TerminalType.FILTER | new AnonymousFunction(new FunParameters([], new BasicType(TerminalType.NUMBER)), new StatementBlock([])) | '.filter((): Number => {})'
    }

    @Unroll
    def "should return unexpected token when input is incorrect for string '#inputString'"() {
        given:
        lazyLexer = buildLazyLexer(inputString)

        and:
        def bufferedLexer = new BufferedLexer(lazyLexer)

        when:
        ListModifierParser.INSTANCE.parse(bufferedLexer)

        then:
        def exception = thrown(UnexpectedToken)
        exception.message.contains(expectedToken.toString())

        where:
        expectedToken            | inputString
        TerminalType.FILTER      | "filter(isEven())"
        TerminalType.CLOSE_PAREN | ".filter()"
        TerminalType.CLOSE_PAREN | ".filter)"
        new IdToken("isEven")    | ".filter isEven)"
    }
}


