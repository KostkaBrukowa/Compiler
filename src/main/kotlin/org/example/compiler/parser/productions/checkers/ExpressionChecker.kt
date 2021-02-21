package org.example.compiler.parser.productions.checkers

import org.example.compiler.parser.Scope
import org.example.compiler.lexer.token.TerminalType
import org.example.compiler.parser.Production
import org.example.compiler.parser.productions.*
import org.example.compiler.parser.productions.subproductions.TypeObject

class ExpressionChecker {
    fun checkInvariants(expressionType: TypeObject, innerExpressions: List<Production>, scope: Scope) {
        innerExpressions.forEach { multiplicativeExpression ->
            multiplicativeExpression.checkInvariants(scope)

            if (multiplicativeExpression.getType(scope) != expressionType)
                throw ExpressionTypeMismatch(expressionType, multiplicativeExpression.getType(scope))
        }
    }

    fun checkAvailableOperators(operators: List<TerminalType>, availableOperators: List<TerminalType>) {
        if (!availableOperators.containsAll(operators)) throw TypeError("Operator not available")
    }
}

class ExpressionExecutor {
    fun handleStringExpression(executedExpressions: List<Production>): StringLiteral? = StringLiteral(
        executedExpressions.fold("") { acc, executedExpression ->
            if (executedExpression !is StringLiteral) throw ExpressionExecutionError(executedExpression)

            acc + executedExpression.value
        })

    fun handleListExpression(executedExpressions: List<Production>): ListLiteral? = ListLiteral(
        executedExpressions.fold(emptyList()) { acc, executedExpression ->
            if (executedExpression !is ListLiteral) throw ExpressionExecutionError(executedExpression)

            acc + executedExpression.elements
        })

    fun handleNumberExpression(
        executedExpressions: List<Production>,
        operators: List<TerminalType>,
        operatorHandler: (acc: Float, nextValue: Float, operator: TerminalType) -> Float
    ): NumberLiteral? {
        val firstElement = executedExpressions.first()

        if (firstElement !is NumberLiteral) throw ExpressionExecutionError(firstElement)
        if (executedExpressions.size == 1) return firstElement

        val expressionsWithOperators = executedExpressions.drop(1).zip(operators)

        return NumberLiteral(expressionsWithOperators.fold(firstElement.value) { acc, (executedExpression, operator) ->
            if (executedExpression !is NumberLiteral) throw ExpressionExecutionError(executedExpression)

            operatorHandler(acc, executedExpression.value, operator)
        })
    }

}
