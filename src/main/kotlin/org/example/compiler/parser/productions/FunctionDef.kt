package org.example.compiler.parser.productions

import org.example.compiler.parser.Scope
import org.example.compiler.parser.Production
import org.example.compiler.parser.productions.checkers.FunctionChecker
import org.example.compiler.parser.productions.subproductions.TypeObject

interface Function : Production {
    val parameters: FunParameters
    val block: StatementBlock
}

data class FunctionDef(
    override val id: String,
    override val parameters: FunParameters,
    override val block: StatementBlock
) : SingleStatement, IdReferenceHolder, Function {
    private val functionChecker: FunctionChecker = FunctionChecker(parameters, block)

    override fun execute(scope: Scope): Production? = functionChecker.execute(scope)
    override fun checkInvariants(scope: Scope) = functionChecker.checkInvariants(scope)
    override fun getType(scope: Scope): TypeObject = parameters.getType(scope)

    override fun getAllReturnTypes(scope: Scope): List<TypeObject> = functionChecker.getAllReturnTypes(scope)
}


open class TypeError(s: String) : Throwable(s)
class InvariantError(s: String) : Throwable(s)
