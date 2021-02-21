package org.example.compiler.lexer.recognisers

import org.example.compiler.lexer.token.StringToken
import org.example.compiler.lexer.token.UnclosedStringToken
import org.example.compiler.source.Source
import spock.lang.Specification
import spock.lang.Unroll

class StringRecogniserTest extends Specification {
    def recogniser = new StringRecogniser()

    @Unroll
    def "should return correct string #result from `#inputString`"() {
        expect:
        recogniser.recognise(new Source(new StringReader(inputString))) == result

        where:
        inputString                                     | result
        "\"jarek napisał ten string\""                  | "\"jarek napisał ten string\""
        "\"jarek napisał ten string ale nie zamknął"    | "\"jarek napisał ten string ale nie zamknął"
        "\"\""                                          | "\"\""
        "\"napisał i był enter a za nim zamnknął \n \"" | "\"napisał i był enter a za nim zamnknął "
        "\"tylko ta część\" \"a nie ta\""               | "\"tylko ta część\""
        "nie \" zaczyna się od \""                      | null
        " \"ie \" zaczyna się od \""                    | null
        " \"ieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieieie\""                    | null
    }

    def "should return correct type string token based on string"() {
        when:
        def result = recogniser.convertToToken("\"jarek napisał ten string\"")

        then:
        result instanceof StringToken
        (result as StringToken).lexeme == "jarek napisał ten string"
    }

    def "should return correct unclosed type string token based on lexeme"() {
        when:
        def result = recogniser.convertToToken("\"jarek napisał ten string ale nie zamknął")

        then:
        result instanceof UnclosedStringToken
        (result as UnclosedStringToken).lexeme == "jarek napisał ten string ale nie zamknął"
    }
}
