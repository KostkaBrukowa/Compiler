package org.example.compiler.lexer.recognisers


import org.example.compiler.lexer.token.TerminalToken
import org.example.compiler.source.Source
import spock.lang.Specification
import spock.lang.Unroll

class TerminalRecogniserTest extends Specification {
    def recogniser = new TerminalRecogniser()

    @Unroll
    def "should return correct terminal #result from `#inputString`"() {
        expect:
        recogniser.recognise(new Source(new StringReader(inputString))) == result

        where:
        inputString | result
        "{---  "    | "{"
        "=="        | "=="
        "=>"        | "=>"
        "=<"        | "="
        "^^^^^"     | "^"
        "+ --"      | "+"
        "-+"        | "-"
        "-"         | "-"
        ";"         | ";"
        "{"         | "{"
        ""          | null
        "a}"        | null
        " +"        | null
    }

    @Unroll
    def "should return correct type token based on lexeme"() {
        expect:
        recogniser.convertToToken(inputString) instanceof TerminalToken

        where:
        inputString << ["=", "-", "==", "}", "{"]
    }
}
