package org.example.compiler.parser.productions

import org.example.compiler.parser.Scope
import org.example.compiler.parser.Production
import org.example.compiler.parser.productions.subproductions.TypeObject

data class IfStatement(
    val condition: Condition,
    val statement: Statement,
    val elseStatement: Statement? = null
) : SingleStatement {
    override fun execute(scope: Scope): Production? =
        if (condition.execute(scope).isTrue)
            statement.execute(scope)
        else elseStatement?.execute(scope)

    override fun checkInvariants(scope: Scope) {
        condition.checkInvariants(scope)
        statement.checkInvariants(scope)
        elseStatement?.checkInvariants(scope)
    }

    override fun getAllReturnTypes(scope: Scope): List<TypeObject> {
        val statementReturns = statement.getAllReturnTypes(scope)
        val elseReturns = elseStatement?.getAllReturnTypes(scope) ?: emptyList()

        return statementReturns + elseReturns
    }
}

