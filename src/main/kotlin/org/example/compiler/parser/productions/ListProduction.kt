package org.example.compiler.parser.productions

import org.example.compiler.parser.Scope
import org.example.compiler.lexer.token.TerminalType
import org.example.compiler.parser.Production
import org.example.compiler.parser.productions.subproductions.TypeObject

data class ListLiteral(val elements: List<NumberLiteral>) : Production

data class ListProduction(val elements: List<Expression>) : Literal, ListAsValue {
    override fun execute(scope: Scope): ListLiteral {
        val executedElements = elements.map {
            when (val executionResult = it.execute(scope)) {
                is NumberLiteral -> executionResult
                else -> throw TypeError("Got $executionResult instead of Number in list")
            }
        }

        return ListLiteral(executedElements)
    }

    override fun getType(scope: Scope): TypeObject = BasicType(TerminalType.LIST)

    override fun checkInvariants(scope: Scope) {
        if (elements.any { it.getType(scope) != BasicType(TerminalType.NUMBER) })
            throw TypeError("Elements of the list were not a number")
    }
}

