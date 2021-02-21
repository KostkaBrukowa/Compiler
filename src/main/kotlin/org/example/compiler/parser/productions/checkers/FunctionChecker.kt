package org.example.compiler.parser.productions.checkers

import org.example.compiler.parser.Scope
import org.example.compiler.parser.Production
import org.example.compiler.parser.productions.FunParameters
import org.example.compiler.parser.productions.InvariantError
import org.example.compiler.parser.productions.StatementBlock
import org.example.compiler.parser.productions.TypeError
import org.example.compiler.parser.productions.subproductions.TypeObject

class FunctionChecker(
    private val parameters: FunParameters,
    private val block: StatementBlock,
    id: String? = null
) {
    private val name: String = id ?: "anonymous function"

    fun execute(scope: Scope): Production? = block.execute(scope)

    fun checkInvariants(scope: Scope) {
        block.checkInvariants(scope, parameters.arguments)

        if (getAllReturnTypes(scope).any { parameters.returnType != it }) {
            val wrongReturn = getAllReturnTypes(scope).first { parameters.returnType != it }
            throw TypeError("One of return types of function was incorrect for function $name. ${parameters.returnType} expected, got $wrongReturn")
        }
    }

    fun getAllReturnTypes(scope: Scope): List<TypeObject> {
        val allReturnStatements = block.getAllReturnTypes(scope, parameters.arguments)

        if (allReturnStatements.isEmpty()) {
            throw InvariantError("Return was not declared in the function $name")
        }

        return allReturnStatements
    }

}
