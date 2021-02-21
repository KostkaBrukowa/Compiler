package org.example.compiler.parser

import org.example.compiler.lexer.token.TerminalType
import org.example.compiler.lexer.token.TokenType
import spock.lang.Specification
import spock.lang.Unroll

import static org.example.compiler.utils.TokenUtils.t

class BufferedLexerTest extends Specification {
    Lexer lexer = Mock()
    def lexerOutput = [
            t(TokenType.NUMBER, "0"),
            t(TokenType.NUMBER, "1"),
            t(TokenType.NUMBER, "2"),
            t(TokenType.NUMBER, "3"),
            t(TokenType.NUMBER, "4"),
            t(TokenType.NUMBER, "5"),
            t(TokenType.NUMBER, "6"),
            t(TokenType.NUMBER, "7"),
            t(TokenType.NUMBER, "8"),
            t(TokenType.NUMBER, "9"),
            null,
            null,
            null,
    ]

    def setup() {
        lexer = Mock()
    }

    def "should return correct current meta"() {
        given: "that i'm not very proud of this test"
        lexer.getNextTokenMeta() >>> [t(TerminalType.OPEN_PAREN), t(TerminalType.CARROT), null, null]

        and: "it still gets the job done"
        def bufferedLexer = new BufferedLexer(lexer)

        when:
        def firstCurrentMeta = bufferedLexer.getCurrentTokenMeta()
        def firstNextMeta = bufferedLexer.getNextTokenMeta(1)
        def secondCurrentMeta = bufferedLexer.getCurrentTokenMeta()
        def firstPeekNextMeta = bufferedLexer.peekNextTokenMeta(1)
        def secondPeekNextMeta = bufferedLexer.peekNextTokenMeta(1)
        def thirdPeekNextMeta = bufferedLexer.peekNextTokenMeta(1)
        def thirdCurrentMeta = bufferedLexer.getCurrentTokenMeta()
        def secondNextMeta = bufferedLexer.getNextTokenMeta(1)
        def thirdNextMeta = bufferedLexer.getNextTokenMeta(1)
        def fourthCurrentMeta = bufferedLexer.getCurrentTokenMeta()
        def fourthPeekMeta = bufferedLexer.peekNextTokenMeta(1)

        then:
        firstCurrentMeta == null
        firstNextMeta == t(TerminalType.OPEN_PAREN)
        secondCurrentMeta == firstNextMeta
        firstPeekNextMeta == t(TerminalType.CARROT)
        secondPeekNextMeta == firstPeekNextMeta
        thirdPeekNextMeta == secondPeekNextMeta
        thirdCurrentMeta == secondCurrentMeta
        secondNextMeta == t(TerminalType.CARROT)
        thirdNextMeta == null
        fourthCurrentMeta == null
        fourthPeekMeta == null
    }

    def "should return correct first letter of string"() {
        given:
        lexer.getNextTokenMeta() >>> lexerOutput

        when:
        def bufferedLexer = new BufferedLexer(lexer)

        then:
        bufferedLexer.getCurrentTokenMeta() == null
    }

    @Unroll
    def "should return correct letter of string after number of seeks #numberOfSeeks"() {
        given:
        lexer.getNextTokenMeta() >>> lexerOutput

        and:
        def bufferedLexer = new BufferedLexer(lexer)

        when:
        for (int i = 0; i < numberOfSeeks; i++) {
            bufferedLexer.getNextTokenMeta(1)
        }

        then:
        bufferedLexer.getCurrentTokenMeta() == t(TokenType.NUMBER, letter)

        where:
        numberOfSeeks | letter
        4             | "3"
        6             | "5"
        8             | "7"
        11            | null
        18            | null
    }

    @Unroll
    def "should return correct next char"() {
        given:
        lexer.getNextTokenMeta() >>> lexerOutput

        and:
        def bufferedLexer = new BufferedLexer(lexer)

        when:
        for (int i = 0; i < numberOfSeeks; i++) {
            bufferedLexer.getNextTokenMeta(1)
        }

        then:
        bufferedLexer.getNextTokenMeta(1) == t(TokenType.NUMBER, letter)

        where:
        numberOfSeeks | letter
        4             | "4"
        6             | "6"
        8             | "8"
        10            | null
        18            | null
    }
}
