package org.example.compiler.lexer.recognisers

import org.example.compiler.source.Source
import spock.lang.Specification
import spock.lang.Unroll

class NumberRecogniserTest extends Specification {
    def recogniser = new NumberRecogniser()

    @Unroll
    def "should return correct number #result from `#inputString`"() {
        expect:
        recogniser.recognise(new Source(new StringReader(inputString))) == result

        where:
        inputString                   | result
        "123.00 jak"                  | "123.00"
        "123.0.0 jak"                 | "123.0"
        ".23 sdf"                     | ".23"
        "00.23"                       | "00.23"
        "0.23"                        | "0.23"
        "1 123.00 jak"                | "1"
        "a 123.00 jak"                | null
        ""                            | null
        "100000000000000000"          | "100000000000000000" // 18 chars
        "1000000000000000000"         | null // 19 chars
        "100000000000000000000000000" | null // 24 chars
    }
}
