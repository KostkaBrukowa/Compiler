package org.example.compiler.lexer


import org.example.compiler.lexer.recognisers.NumberRecogniser
import org.example.compiler.lexer.recognisers.SingleWordRecogniser
import org.example.compiler.lexer.recognisers.StringRecogniser
import org.example.compiler.lexer.recognisers.TerminalRecogniser
import org.example.compiler.lexer.token.*
import org.example.compiler.source.Source
import spock.lang.Specification
import spock.lang.Unroll

class LazyLexerTest extends Specification {
    def idRecogniser = Mock(SingleWordRecogniser)
    NumberRecogniser numberRecogniser = Mock()
    StringRecogniser stringRecogniser = Mock()
    TerminalRecogniser terminalRecogniser = Mock()

    // UNIT

    def "should pass correct strings to the recognisers"() {
        given:
        def input = "1 2 3"

        and:
        def source = new Source(new StringReader(input))

        when:
        numberRecogniser.recognise(_ as Source) >> "1" >> "2" >> "3"

        and:
        buildMockLazyLexer(source).nextTokenMeta
        buildMockLazyLexer(source).nextTokenMeta
        buildMockLazyLexer(source).nextTokenMeta

        then:
        1 * numberRecogniser.convertToToken("1") >> new NumberToken(1)
        1 * numberRecogniser.convertToToken("2") >> new NumberToken(2)
        1 * numberRecogniser.convertToToken("3") >> new NumberToken(3)
        0 * idRecogniser.recognise(_)
        0 * stringRecogniser.recognise(_)
        0 * terminalRecogniser.recognise(_)
    }

    def "should pass source to the next recognisers when previous return null"() {
        given:
        def input = "someToken"

        and:
        def source = new Source(new StringReader(input))

        when:
        numberRecogniser.recognise(_ as Source) >> null
        idRecogniser.recognise(_ as Source) >> null
        stringRecogniser.recognise(_ as Source) >> null
        terminalRecogniser.recognise(_ as Source) >> null

        and:
        buildMockLazyLexer(source).nextTokenMeta
        buildMockLazyLexer(source).nextTokenMeta
        buildMockLazyLexer(source).nextTokenMeta

        then:
        1 * numberRecogniser.recognise(_)
        1 * idRecogniser.recognise(_)
        1 * stringRecogniser.recognise(_)
        1 * terminalRecogniser.recognise(_)
    }

    //INTEGRATION

    def "should return single keyword token"() {
        given:
        def input = "String"

        and:
        def source = new Source(new StringReader(input))

        expect:
        buildLazyLexer(source).nextTokenMeta.token == new TerminalToken(TerminalType.STRING)
    }

    def "should return correct position"() {
        given:
        def input = "val \n val \nval"

        and:
        def source = new Source(new StringReader(input))

        and:
        def lexer = buildLazyLexer(source)

        when:
        lexer.nextTokenMeta
        lexer.nextTokenMeta
        def lastToken = lexer.nextTokenMeta

        then:
        with(lastToken.position) {
            column == 1
            row == 3
        }
        lastToken.token == new TerminalToken(TerminalType.VAL)
    }

    @Unroll
    def "should show correct position (#expectedColumn, #expectedRow) after #nextTokenCount next tokens from string #input"() {
        given:
        def source = new Source(new StringReader(input))

        and:
        def lexer = buildLazyLexer(source)

        when:
        for (int i = 0; i < nextTokenCount; i++) {
            lexer.getNextTokenMeta()
        }

        then:
        with(lexer.getNextTokenMeta().position) {
            column == expectedColumn
            row == expectedRow
        }

        where:
        input                                   | nextTokenCount | expectedColumn | expectedRow
        "val \n val \nval"                      | 0              | 1              | 1
        "val \n val \nval"                      | 1              | 2              | 2
        "val \n val \nval"                      | 2              | 1              | 3
        "\n\n\n\ni"                             | 0              | 1              | 5
        "\n  i \n   bal"                        | 0              | 3              | 2
        "\n  i \n   bal"                        | 1              | 4              | 3
        "x = list^filter((it: Number): Boolean" | 0              | 1              | 1
        "x = list^filter((it: Number): Boolean" | 1              | 3              | 1
        "x = list^filter((it: Number): Boolean" | 2              | 5              | 1
        "x = list^filter((it: Number): Boolean" | 3              | 9              | 1
        "x = list^filter((it: Number): Boolean" | 4 | 10 | 1
    }

    @Unroll
    def "should return correct sequence of tokens from string source \"#inputString\""() {
        given:
        def source = new Source(new StringReader(inputString))

        when:
        def lexer = buildLazyLexer(source)

        then:
        for (expectedToken in tokens) {
            def returnedToken = lexer.nextTokenMeta

            assert returnedToken.token == expectedToken
        }

        lexer.nextTokenMeta == null

        where:
        inputString                                                      | tokens
        "val i = 11;"                                                    | [t(TerminalType.VAL),
                                                                            t(TokenType.ID, "i"),
                                                                            t(TerminalType.EQUALS),
                                                                            t(TokenType.NUMBER, "11"),
                                                                            t(TerminalType.SEMICOLON)]
        "val i = 11;// com \n\n\n val"                                   | [t(TerminalType.VAL),
                                                                            t(TokenType.ID, "i"),
                                                                            t(TerminalType.EQUALS),
                                                                            t(TokenType.NUMBER, "11"),
                                                                            t(TerminalType.SEMICOLON), t(TerminalType.VAL)]
        "val i = 11.123;"                                                | [t(TerminalType.VAL),
                                                                            t(TokenType.ID, "i"),
                                                                            t(TerminalType.EQUALS),
                                                                            t(TokenType.NUMBER, "11.123"),
                                                                            t(TerminalType.SEMICOLON)]
        "val i = 11.123;//and a comment here\n val"                      | [t(TerminalType.VAL), t(TokenType.ID, "i"), t(TerminalType.EQUALS),
                                                                            t(TokenType.NUMBER, "11.123"), t(TerminalType.SEMICOLON),
                                                                            t(TerminalType.VAL)]
        "// this is a comment without an endline"                        | []
        "fun foo(id: String): Boolean{val i = 12; return i; \n }"        | [t(TerminalType.FUN), t(TokenType.ID, "foo"), t(TerminalType.OPEN_PAREN),
                                                                            t(TokenType.ID, "id"), t(TerminalType.COLON), t(TerminalType.STRING), t(TerminalType.CLOSE_PAREN),
                                                                            t(TerminalType.COLON), t(TerminalType.BOOLEAN), t(TerminalType.OPEN_BRACKET),
                                                                            t(TerminalType.VAL), t(TokenType.ID, "i"), t(TerminalType.EQUALS),
                                                                            t(TokenType.NUMBER, "12"), t(TerminalType.SEMICOLON), t(TerminalType.RETURN),
                                                                            t(TokenType.ID, "i"), t(TerminalType.SEMICOLON), t(TerminalType.CLOSE_BRACKET)]
        "val list = [1, 2, \"to jest string\"]"                          | [t(TerminalType.VAL), t(TokenType.ID, "list"), t(TerminalType.EQUALS), t(TerminalType.OPEN_SQUARE_BRACKET),
                                                                            t(TokenType.NUMBER, "1"), t(TerminalType.COMMA), t(TokenType.NUMBER, "2"), t(TerminalType.COMMA),
                                                                            t(TokenType.STRING, "to jest string"), t(TerminalType.CLOSE_SQUARE_BRACKET),]
        "val list = [1, 2, \"to jest string\"]"                          | [t(TerminalType.VAL), t(TokenType.ID, "list"), t(TerminalType.EQUALS), t(TerminalType.OPEN_SQUARE_BRACKET),
                                                                            t(TokenType.NUMBER, "1"), t(TerminalType.COMMA), t(TokenType.NUMBER, "2"), t(TerminalType.COMMA),
                                                                            t(TokenType.STRING, "to jest string"), t(TerminalType.CLOSE_SQUARE_BRACKET),]
        "x = list^filter((it: Number): Boolean => { return it == 2 }); " | [t(TokenType.ID, "x"), t(TerminalType.EQUALS), t(TokenType.ID, "list"), t(TerminalType.CARROT),
                                                                            t(TerminalType.FILTER), t(TerminalType.OPEN_PAREN), t(TerminalType.OPEN_PAREN), t(TokenType.ID, "it"),
                                                                            t(TerminalType.COLON), t(TerminalType.NUMBER), t(TerminalType.CLOSE_PAREN),
                                                                            t(TerminalType.COLON), t(TerminalType.BOOLEAN), t(TerminalType.ARROW), t(TerminalType.OPEN_BRACKET),
                                                                            t(TerminalType.RETURN), t(TokenType.ID, "it"), t(TerminalType.EQUALS_EQUALS), t(TokenType.NUMBER, "2"),
                                                                            t(TerminalType.CLOSE_BRACKET), t(TerminalType.CLOSE_PAREN), t(TerminalType.SEMICOLON)]
        "x = \"string that isnt closed"                                  | [t(TokenType.ID, "x"), t(TerminalType.EQUALS), t(TokenType.UNCLOSED_STRING, "string that isnt closed")]
        "x == 1.11.11.11."                                               | [t(TokenType.ID, "x"), t(TerminalType.EQUALS_EQUALS), t(TokenType.NUMBER, "1.11"), t(TokenType.NUMBER, ".11"),
                                                                            t(TokenType.NUMBER, ".11"), t(TerminalType.DOT)]
        "x #=\$"                                                         | [t(TokenType.ID, "x"), t(TokenType.UNRECOGNISED_TOKEN, "#"), t(TerminalType.EQUALS), t(TokenType.UNRECOGNISED_TOKEN, "\$"),]
    }

    private static def t(Object type, String lexeme = "") {
        if (type instanceof TerminalType) return new TerminalToken(type)
        if (type instanceof TokenType) {
            if (type == TokenType.ID) return new IdToken(lexeme)
            if (type == TokenType.NUMBER) return new NumberToken(lexeme.toDouble())
            if (type == TokenType.STRING) return new StringToken(lexeme)
            if (type == TokenType.UNCLOSED_STRING) return new UnclosedStringToken(lexeme)
            if (type == TokenType.UNRECOGNISED_TOKEN) return new UnrecognisedToken(lexeme)
        }

        return null
    }

    static def buildLazyLexer(Source source) {
        return new LazyLexer(
                source,
                new SingleWordRecogniser(),
                new NumberRecogniser(),
                new StringRecogniser(),
                new TerminalRecogniser()
        )
    }

    static def buildLazyLexer(String string) {
        return buildLazyLexer(new Source(new StringReader(string)))
    }

    private def buildMockLazyLexer(Source source) {
        return new LazyLexer(
                source,
                idRecogniser,
                numberRecogniser,
                stringRecogniser,
                terminalRecogniser
        )
    }
}
