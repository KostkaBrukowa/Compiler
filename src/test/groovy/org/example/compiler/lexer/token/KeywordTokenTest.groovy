package org.example.compiler.lexer.token

import spock.lang.Specification
import spock.lang.Unroll

import static org.example.compiler.lexer.token.TerminalTokenKt.isTerminalKeyword
import static org.example.compiler.lexer.token.TerminalTokenKt.toTerminalToken

class KeywordTokenTest extends Specification {
    @Unroll
    def "should map string #typeString to correct type #type"() {
        expect:
        toTerminalToken(typeString).type == type

        where:
        typeString | type
        "return"   | TerminalType.RETURN
        "filter"   | TerminalType.FILTER
        "val"      | TerminalType.VAL
        "concat"   | TerminalType.CONCAT
        "map"      | TerminalType.MAP
        "filter"   | TerminalType.FILTER
        "fun"      | TerminalType.FUN
    }

    @Unroll
    def "should return correct boolean #result depending on string #typeString"() {
        expect:
        isTerminalKeyword(typeString) == result

        where:
        typeString | result
        "return"   | true
        "maper"    | false
        "filter"   | true
        " return"  | false
        "Fun"      | false
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
