package org.example.compiler.lexer.token

import spock.lang.Specification
import spock.lang.Unroll

import static org.example.compiler.lexer.token.TerminalTokenKt.isTerminalKeyword
import static org.example.compiler.lexer.token.TerminalTokenKt.toTerminalToken


class TypeTokenTest extends Specification {

    @Unroll
    def "should map string #typeString to correct type #type"() {
        expect:
        toTerminalToken(typeString).type == type

        where:
        typeString | type
        "Number"   | TerminalType.NUMBER
        "String"   | TerminalType.STRING
        "List"     | TerminalType.LIST
        "Function" | TerminalType.FUNCTION
    }

    @Unroll
    def "should return correct boolean #result depending on string #typeString"() {
        expect:
        isTerminalKeyword(typeString) == result

        where:
        typeString | result
        "Unit"     | false
        "number"   | false
        "String"   | true
        "ist"      | false
        "fun"      | true
    }

    @Unroll
    def "should throw an error when string #typeString is not a type"() {
        when:
        toTerminalToken(typeString)

        then:
        thrown(InternalError)

        where:
        typeString << ["unit", "Numbe", "Sting", "", "\\"]
    }
}
