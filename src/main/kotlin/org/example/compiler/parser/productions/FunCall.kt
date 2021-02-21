package org.example.compiler.parser.productions

import org.example.compiler.parser.HolderWithValue
import org.example.compiler.parser.Scope
import org.example.compiler.parser.Production
import org.example.compiler.parser.productions.subproductions.Assignable


data class FunCall(val arguments: List<Assignable>) : Assignable, ListModifierCallback, PrimaryExpression,
    IdReferenceArgument {
    override fun execute(scope: Scope): Production? {
        throw RuntimeException("call to fun call execute")
    }

    fun execute(scope: Scope, holderWithValue: HolderWithValue): Production? {
        val calee = if (holderWithValue.holder is Function)
            holderWithValue.holder else holderWithValue.value as AnonymousFunction
        val caleeId = holderWithValue.holder.id

        if (calee.getType(scope) !is FunctionType) throw CallToNonFunctionError(calee.getType(scope), "$calee")

        if (calee.parameters.arguments.size != arguments.size) throw InvalidFunctionCallError(calee.toString())

        val callArgumentsValues = arguments
            .zip(calee.parameters.arguments)
            .map { pair ->
                val (argument, parameter) = pair
                val argumentType = argument.getType(scope)

                if (argumentType is FunctionType) {
                    HolderWithValue(parameter, argument)
                } else HolderWithValue(
                    parameter,
                    argument.execute(scope)
                )
            }

        val currentFunctionScope = scope.getScopeForId(caleeId) ?: throw UndeclaredVariableError(caleeId)
        val newFunctionScope = currentFunctionScope.copy().addAllHoldersWithValuesToCurrentBlock(callArgumentsValues)

        return calee.execute(newFunctionScope)
    }


    fun checkFunctionCall(argument: FunCall, holderType: FunctionType, scope: Scope) {
        val functionArgumentsTypes = holderType.argumentsTypes
        val functionCallTypes = argument.arguments.map { it.getType(scope) }

        if (functionArgumentsTypes.size != functionCallTypes.size ||
            functionArgumentsTypes.zip(functionCallTypes).any { it.first != it.second }
        )
            throw InvalidFunctionCallError(holderType.toString())
    }
}

