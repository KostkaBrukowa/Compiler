package org.example.compiler.interpreter

import org.example.compiler.lexer.token.TerminalType
import org.example.compiler.parser.IdentifierRedefinitionException
import org.example.compiler.parser.Scope
import org.example.compiler.parser.productions.ArgumentWithType
import org.example.compiler.parser.productions.BasicType
import spock.lang.Specification

class ScopeTest extends Specification {
    def "scope should have initially 1 block inside"() {
        given:
        def scope = new Scope()

        when:
        scope.pushNewBlock()

        then:
        scope.identifiersBlocks.size() == 1
    }

    def "should return new scope with incremented block count without modifying initial scope"() {
        given:
        def scope = new Scope()

        when:
        def newScope = scope.pushNewBlock()

        then:
        scope.identifiersBlocks.size() == 1
        newScope.identifiersBlocks.size() == 2
    }

    def "should hold new pushed holder in last scope block"() {
        given:
        def scope = new Scope()
        def idHolder = new ArgumentWithType("123", new BasicType(TerminalType.STRING))

        when:
        def newScope = scope.pushNewBlock()
        def newScope1 = newScope.pushNewBlock(idHolder)

        then:
        newScope1.identifiersBlocks.size() == 3
        newScope1.identifiersBlocks[-1]["123"].holder == idHolder
    }

    def "should return correct holder fro get latest and get current when pushed holder is in the last block"() {
        given:
        def scope = new Scope()
        def idHolder = new ArgumentWithType("123", new BasicType(TerminalType.STRING))

        when:
        def newScope = scope.pushNewBlock(idHolder)

        then:
        newScope.identifiersBlocks.size() == 2
        newScope.getLatestIdHolder("123") == idHolder
    }

    def "should return correct holder from get latest and null from get current when pushed holder is in not in the last block"() {
        given:
        def scope = new Scope()
        def idHolder = new ArgumentWithType("123", new BasicType(TerminalType.STRING))

        when:
        def newScope = scope.pushNewBlock(idHolder)
        def newScopeWithoutHolder = newScope.pushNewBlock()

        then:
        newScopeWithoutHolder.identifiersBlocks.size() == 3
        newScopeWithoutHolder.getLatestIdHolder("123") == idHolder
    }

    def "should modify current block"() {
        given:
        def scope = new Scope()
        def idHolder = new ArgumentWithType("123", new BasicType(TerminalType.STRING))
        def newScope = scope.pushNewBlock(idHolder)

        when:
        def newHolder = new ArgumentWithType("newOne", new BasicType(TerminalType.STRING))

        and:
        newScope.addHolderToCurrentBlock(newHolder)

        then:
        newScope.identifiersBlocks.size() == 2
        newScope.getLatestIdHolder("123") == idHolder
        newScope.getLatestIdHolder("newOne") == newHolder
    }

    def "should modify current block with multiple holders"() {
        given:
        def scope = new Scope()
        def idHolder = new ArgumentWithType("first", new BasicType(TerminalType.STRING))
        def idHolder1 = new ArgumentWithType("second", new BasicType(TerminalType.STRING))
        def idHolder2 = new ArgumentWithType("third", new BasicType(TerminalType.STRING))

        when:
        scope.addAllHoldersToCurrentBlock([idHolder, idHolder1, idHolder2])

        then:
        scope.identifiersBlocks.size() == 1
        scope.getLatestIdHolder("first") == idHolder
        scope.getLatestIdHolder("second") == idHolder1
        scope.getLatestIdHolder("third") == idHolder2
    }

    def "should not modify current block with multiple holders when ids collide"() {
        given:
        def scope = new Scope()
        def idHolder = new ArgumentWithType("first", new BasicType(TerminalType.STRING))
        def idHolder1 = new ArgumentWithType("second", new BasicType(TerminalType.STRING))
        def idHolder2 = new ArgumentWithType("first", new BasicType(TerminalType.STRING))

        when:
        scope.addAllHoldersToCurrentBlock([idHolder, idHolder1, idHolder2])

        then:
        thrown(IdentifierRedefinitionException)
    }

    def "should throw when variable is already defined in current block"() {
        given:
        def scope = new Scope()
        def idHolder = new ArgumentWithType("oldOne", new BasicType(TerminalType.STRING))
        def newScope = scope.pushNewBlock(idHolder)

        when:
        def newHolder = new ArgumentWithType("oldOne", new BasicType(TerminalType.STRING))

        and:
        newScope.addHolderToCurrentBlock(newHolder)

        then:
        thrown(IdentifierRedefinitionException)
    }

    def "should return correct scope for id"() {
        given:
        def scope = new Scope()
        def idHolder1 = new ArgumentWithType("1", new BasicType(TerminalType.STRING))
        def idHolder2 = new ArgumentWithType("2", new BasicType(TerminalType.STRING))
        def idHolder3 = new ArgumentWithType("3", new BasicType(TerminalType.STRING))
        def idHolder4 = new ArgumentWithType("4", new BasicType(TerminalType.STRING))

        when:
        def newScope1 = scope.pushNewBlock(idHolder1)
        def newScope2 = newScope1.pushNewBlock(idHolder2)
        def newScope3 = newScope2.pushNewBlock(idHolder3)
        def newScope4 = newScope3.pushNewBlock(idHolder4)

        then:
        newScope4.getScopeForId("2").getLatestIdHolder("2") == idHolder2
        newScope4.getScopeForId("2").getLatestIdHolder("1") == idHolder1
        newScope4.getScopeForId("2").getLatestIdHolder("3") == null
        newScope4.getScopeForId("3").getLatestIdHolder("2") == idHolder2
        newScope4.getScopeForId("3").getLatestIdHolder("1") == idHolder1
        newScope4.getScopeForId("3").getLatestIdHolder("3") == idHolder3
    }

}
