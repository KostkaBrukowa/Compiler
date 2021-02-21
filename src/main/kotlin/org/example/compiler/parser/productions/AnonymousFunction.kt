package org.example.compiler.parser.productions

import org.example.compiler.parser.Scope
import org.example.compiler.parser.Production
import org.example.compiler.parser.productions.checkers.FunctionChecker
import org.example.compiler.parser.productions.subproductions.Assignable
import org.example.compiler.parser.productions.subproductions.TypeObject

data class AnonymousFunction(
    override val parameters: FunParameters,
    override val block: StatementBlock
) : Assignable, ListModifierCallback, Function {
    private val functionChecker: FunctionChecker = FunctionChecker(parameters, block)

    override fun execute(scope: Scope): Production? = functionChecker.execute(scope)
    override fun checkInvariants(scope: Scope) = functionChecker.checkInvariants(scope)
    override fun getType(scope: Scope): TypeObject = parameters.getType(scope)
}

