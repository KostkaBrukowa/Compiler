package org.example.compiler.parser.productions


import org.example.compiler.parser.Scope
import org.example.compiler.lexer.token.TerminalType
import org.example.compiler.parser.Production
import org.example.compiler.parser.ProductionParser
import org.example.compiler.parser.productions.parsers.FunCallParser
import org.example.compiler.parser.productions.parsers.IndexingReferenceParser
import org.example.compiler.parser.productions.subproductions.TypeObject
import org.example.compiler.parser.productions.utils.OrParser

interface IdReferenceArgument : Production
interface IdReferenceHolder : Production {
    val id: String
}

object IdReferenceArgumentParser : OrParser<IdReferenceArgument> {
    override val parsers: List<ProductionParser<out IdReferenceArgument>> = listOf(
        IndexingReferenceParser,
        FunCallParser
    )
}

data class IdReference(
    val id: String,
    val argument: IdReferenceArgument? = null
) : PrimaryExpression, ListModifierCallback, ListAsValue, ConditionElement {
    override fun execute(scope: Scope): Production? {
        val idHolder = scope.getLatestId(id) ?: throw UndeclaredVariableError(id)

        return when (argument) {
            is IndexingReference -> argument.execute(scope, idHolder)
            is FunCall -> argument.execute(scope, idHolder)
            null -> idHolder.value
            else -> throw ExecutionError(this)
        }
    }

    override fun getType(scope: Scope): TypeObject {
        val holderType = getHolder(scope).getType(scope)

        if (argument == null) return holderType

        return when (holderType) {
            is FunctionType -> holderType.returnType
            BasicType(TerminalType.LIST) -> BasicType(TerminalType.NUMBER)
            else -> holderType
        }
    }

    override fun checkInvariants(scope: Scope) {
        val holder = getHolder(scope)
        val holderType = holder.getType(scope)

        when (argument) {
            is FunCall -> checkFunCall(argument, holderType, scope)
            is IndexingReference -> checkIndexingOperator(argument, holderType, scope)
            null -> return
        }
    }

    private fun checkIndexingOperator(argument: IndexingReference, holderType: TypeObject, scope: Scope) {
        if (holderType != BasicType(TerminalType.LIST))
            throw IndexingOfNonListError(holderType)

        argument.checkInvariants(scope)
    }

    private fun checkFunCall(argument: FunCall, holderType: TypeObject, scope: Scope) {
        if (holderType !is FunctionType)
            throw CallToNonFunctionError(holderType, "$argument")

        argument.checkFunctionCall(argument, holderType, scope)
    }

    private fun getHolder(scope: Scope): IdReferenceHolder =
        scope.getLatestIdHolder(id) ?: throw UndeclaredVariableError(id)

}

open class ExecutionError(production: Production, extraInfo: String = "") :
    Throwable("There was an error in production executing. $production. $extraInfo")

class InvalidFunctionCallError(extraInfo: String) :
    TypeError("Called a function with wrong set of arguments. ExpectedType $extraInfo")

class UndeclaredVariableError(id: String) : TypeError("Undeclared variable: -> $id")
class IndexingOfNonListError(holderType: TypeObject) : TypeError("Indexing of non list. Got $holderType instead")
class CallToNonFunctionError(holderType: TypeObject, extraInfo: String = "") :
    TypeError("Call to a variable that is not a function. Got $holderType instead. $extraInfo")

