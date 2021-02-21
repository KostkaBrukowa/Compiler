package org.example.compiler.parser.productions

import org.example.compiler.parser.HolderWithValue
import org.example.compiler.parser.Scope
import org.example.compiler.lexer.token.TerminalType
import org.example.compiler.parser.Production
import org.example.compiler.parser.ProductionParser
import org.example.compiler.parser.productions.parsers.AnonymousFunctionParser
import org.example.compiler.parser.productions.parsers.IdReferenceParser
import org.example.compiler.parser.productions.subproductions.TypeObject
import org.example.compiler.parser.productions.utils.OrParser

interface ListModifierCallback : Production {
    object Parser : OrParser<ListModifierCallback> {
        override val parsers: List<ProductionParser<out ListModifierCallback>> = listOf(
            IdReferenceParser,
            AnonymousFunctionParser
        )
    }
}


data class ListModifier(val modifier: TerminalType, val callback: ListModifierCallback) : Production {

    fun execute(scope: Scope, list: ListLiteral): ListLiteral {
        if (callback is IdReference && callback.argument is FunCall)
            return ListModifier(modifier, callback.execute(scope) as AnonymousFunction).execute(scope, list)

        return when (modifier) {
            TerminalType.MAP -> mapList(list, scope)
            TerminalType.FILTER -> filterList(list, scope)
            else -> throw ModifierNotSupported(modifier)
        }
    }

    override fun checkInvariants(scope: Scope) {
        val callbackType = callback.getType(scope)

        if (callbackType !is FunctionType)
            throw InvalidListModifierCallback("List modifier callback was not a function")
        if (callbackType.argumentsTypes.size != 1)
            throw InvalidListModifierCallback("List modifier callback definition was incorrect $callbackType")

        val callbackArgument = callbackType.argumentsTypes.first()
        val callbackReturnArgument = callbackType.returnType

        if ((modifier == TerminalType.MAP && isMapCallInvalid(callbackArgument, callbackReturnArgument))
            || (modifier == TerminalType.FILTER && isFilterCallInvalid(callbackArgument, callbackReturnArgument))
        )
            throw InvalidListModifierCallback("List modifier callback definition was incorrect $callbackType for modifier $modifier")
    }

    private fun isFilterCallInvalid(argument: TypeObject, returnArgument: TypeObject): Boolean {
        return argument != BasicType(TerminalType.NUMBER) || returnArgument != BasicType(TerminalType.BOOLEAN)
    }

    private fun isMapCallInvalid(argument: TypeObject, returnArgument: TypeObject): Boolean {
        return argument != BasicType(TerminalType.NUMBER) || returnArgument != BasicType(TerminalType.NUMBER)
    }

    private fun filterList(list: ListLiteral, scope: Scope): ListLiteral {
        val executionScope = buildScopeForTransform(scope)

        val result: List<NumberLiteral> = list.elements.filter {
            when (val predicateResult = useCallback(it, executionScope)) {
                is TrueFalse -> predicateResult.isTrue
                else -> throw InvalidListModifierCallback("Not a boolean returned from function")
            }
        }

        return ListLiteral(result)
    }

    private fun mapList(list: ListLiteral, scope: Scope): ListLiteral {
        val executionScope = buildScopeForTransform(scope)

        val mappedElements = list.elements.map {
            when (val mappedElement = useCallback(it, executionScope)) {
                is NumberLiteral -> mappedElement
                else -> throw InvalidListModifierCallback("Not a number returned from function")
            }
        }

        return ListLiteral(mappedElements)
    }

    private fun buildScopeForTransform(scope: Scope): Scope = when (callback) {
        is AnonymousFunction -> scope.pushNewBlock(callback.holder(scope))
        else -> scope
    }

    private fun getCallbackId() = if (callback is IdReference) callback.id else ANONYMOUS_FUNCTION_ID

    private fun useCallback(number: NumberLiteral, executionScope: Scope): Production {
        val funCall = IdReference(getCallbackId(), FunCall(listOf(number)))

        return funCall.execute(executionScope) ?: throw NullFromNotNullableProduction(funCall)
    }

    private fun AnonymousFunction.holder(scope: Scope) =
        HolderWithValue(
            ArgumentWithType(
                ANONYMOUS_FUNCTION_ID,
                parameters.getType(scope)
            ), callback
        )

    companion object {
        const val ANONYMOUS_FUNCTION_ID: String = "_ANONYMOUS_REFERENCE"
    }
}

class ModifierNotSupported(modifier: TerminalType) : TypeError("Modifier $modifier not supported on the list")
class InvalidListModifierCallback(extraInfo: String) : TypeError(extraInfo)

