package org.example.compiler.lexer.recognisers

import org.example.compiler.lexer.token.IdToken
import org.example.compiler.lexer.token.TerminalToken
import org.example.compiler.source.Source
import spock.lang.Specification
import spock.lang.Unroll

class SingleWordRecogniserTest extends Specification {
    def recogniser = new SingleWordRecogniser()

    @Unroll
    def "should properly recognise ids #inputString"() {
        given:
        def source = new Source(new StringReader(inputString))

        expect:
        recogniser.recognise(source) == result

        where:
        inputString   | result
        "foo bar baz" | "foo"
        "id1"         | "id1"
        "i"           | "i"
        ""            | null
        "&id"         | null
        "\$id"        | null
        "Function"    | "Function"
        "fun1ction"   | "fun1ction"
        "Str ing"     | "Str"
        "String"      | "String"
        "Unit"        | "Unit"
        ""            | null
        "1map"        | null
        "*"           | null
    }

    @Unroll
    def "should return correct type token based on keyword"() {
        expect:
        recogniser.convertToToken(inputString) instanceof TerminalToken

        where:
        inputString << ["String", "Function", "List", "Number", "concat", "filter", "map", "fun"]
    }

    @Unroll
    def "should return correct id token based on keyword"() {
        expect:
        recogniser.convertToToken(inputString) instanceof IdToken

        where:
        inputString << ["foo", "BARR", "J11B", "Sap"]
    }
}
