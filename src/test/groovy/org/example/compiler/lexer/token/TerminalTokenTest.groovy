package org.example.compiler.lexer.token

import spock.lang.Specification
import spock.lang.Unroll

import static org.example.compiler.lexer.token.TerminalTokenKt.isTerminalKeyword
import static org.example.compiler.lexer.token.TerminalTokenKt.toTerminalToken

class TerminalTokenTest extends Specification {

    @Unroll
    def "should map string #typeString to correct type #type"() {
        expect:
        toTerminalToken(typeString).type == type

        where:
        typeString | type
        "="        | TerminalType.EQUALS
        "=="       | TerminalType.EQUALS_EQUALS
        ";"        | TerminalType.SEMICOLON
        ")"        | TerminalType.CLOSE_PAREN
        "{"        | TerminalType.OPEN_BRACKET
    }

    @Unroll
    def "should return correct boolean #result depending on string #typeString"() {
        expect:
        isTerminalKeyword(typeString) == result

        where:
        typeString | result
        "="        | true
        "==="      | false
        "=>"       | true
        ">=="      | false
        "<=="      | false
    }

    @Unroll
    def "should throw an error when string #typeString is not a type"() {
        when:
        toTerminalToken(typeString)

        then:
        thrown(InternalError)

        where:
        typeString << ["===", "Numbe", "Sting", "", "\\"]
    }
}
