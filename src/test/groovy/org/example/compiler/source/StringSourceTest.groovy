package org.example.compiler.source

import spock.lang.Specification
import spock.lang.Unroll

class StringSourceTest extends Specification {
    def "should return correct first letter of string"() {
        given:
        def string = "0123456789"

        when:
        def stringSource = new Source(new StringReader(string))

        then:
        stringSource.getCurrentChar() == '0'
    }

    @Unroll
    def "should return correct letter of string after number of seeks #numberOfSeeks"() {
        given:
        def string = "0123456789"

        and:
        def stringSource = new Source(new StringReader(string))

        when:
        for (int i = 0; i < numberOfSeeks; i++) {
            stringSource.seekForward(1)
        }

        then:
        stringSource.getCurrentChar() == letter

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
        def string = "0123456789"

        and:
        def stringSource = new Source(new StringReader(string))

        when:
        for (int i = 0; i < numberOfSeeks; i++) {
            stringSource.seekForward(1)
        }

        then:
        stringSource.peekNextChar(1) == letter

        where:
        numberOfSeeks | letter
        4             | "4"
        6             | "6"
        8             | "8"
        10            | null
        18            | null
    }
}
