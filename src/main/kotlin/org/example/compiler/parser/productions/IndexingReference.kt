package org.example.compiler.parser.productions

import org.example.compiler.parser.HolderWithValue
import org.example.compiler.parser.Scope
import org.example.compiler.lexer.token.TerminalType
import org.example.compiler.parser.Production

data class IndexingReference(val expression: Expression) : PrimaryExpression, ConditionElement, IdReferenceArgument {
    override fun checkInvariants(scope: Scope) {
        if (expression.getType(scope) != BasicType(TerminalType.NUMBER))
            throw IndexingReferenceExpressionNotANumberError(expression, scope)
    }

    fun execute(scope: Scope, holderWithValue: HolderWithValue): Production? {
        val index = (expression.execute(scope) as NumberLiteral).value.toInt()

        return (holderWithValue.value as ListLiteral).elements[index].execute(scope)
    }
}

class IndexingReferenceExpressionNotANumberError(expression: Expression, scope: Scope) :
    TypeError("Indexing reference argument ([$expression]) was not a number. Got ${expression.getType(scope)}")


