package org.example.compiler.parser.productions

import org.example.compiler.parser.Scope
import org.example.compiler.lexer.token.TerminalType
import org.example.compiler.parser.Production
import org.example.compiler.parser.productions.checkers.ExpressionChecker
import org.example.compiler.parser.productions.checkers.ExpressionExecutor
import org.example.compiler.parser.productions.subproductions.Assignable
import org.example.compiler.parser.productions.subproductions.TypeObject


data class Expression(
    val expressions: List<MultiplicativeExpression>,
    val operators: List<TerminalType>
) : Assignable, ListAsValue {
    private val checker: ExpressionChecker = ExpressionChecker()
    private val executor: ExpressionExecutor = ExpressionExecutor()

    override fun execute(scope: Scope): Production? {
        val expressionType = getType(scope)
        val executedExpressions = expressions.map {
            it.execute(scope) ?: throw ExecutionError(it, "Couldnt execute expression value")
        }

        if (executedExpressions.size == 1) return executedExpressions.first()

        return when (expressionType) {
            BasicType(TerminalType.STRING) -> executor.handleStringExpression(executedExpressions)
            BasicType(TerminalType.NUMBER) -> executor.handleNumberExpression(
                executedExpressions,
                operators,
                this::calculateNumber
            )
            BasicType(TerminalType.LIST) -> executor.handleListExpression(executedExpressions)
            else -> throw RuntimeException("$this Unsupported expression type: $expressionType")
        }
    }

    override fun checkInvariants(scope: Scope) {
        val type = getType(scope)
        checker.checkInvariants(type, expressions, scope)

        if (type == BasicType(TerminalType.STRING) || type == BasicType(TerminalType.LIST))
            checker.checkAvailableOperators(operators, listOf(TerminalType.PLUS))
    }

    override fun getType(scope: Scope): TypeObject = getFirstElement().getType(scope)

    fun getFirstElement(): PrimaryExpression = expressions.first().getFirstElement()

    private fun calculateNumber(acc: Float, nextValue: Float, operator: TerminalType): Float {
        return when (operator) {
            TerminalType.PLUS -> acc + nextValue
            TerminalType.MINUS -> acc - nextValue
            else -> throw ExpressionExecutionError(this, "Operator not allowed $operator")
        }
    }
}


data class MultiplicativeExpression(
    val expressions: List<PrimaryExpression>,
    val operators: List<TerminalType>
) : Production {
    private val checker: ExpressionChecker = ExpressionChecker()
    private val executor: ExpressionExecutor = ExpressionExecutor()

    override fun execute(scope: Scope): Production? {
        val expressionType = getType(scope)
        val executedExpressions = expressions.map {
            it.execute(scope) ?: throw ExecutionError(it, "Couldnt execute expression value")
        }

        if (executedExpressions.size == 1) return executedExpressions.first()

        return when (expressionType) {
            BasicType(TerminalType.NUMBER) -> executor.handleNumberExpression(
                executedExpressions,
                operators,
                this::calculateNumber
            )
            else -> throw RuntimeException("$this Unsupported expression type: $expressionType")
        }
    }

    override fun checkInvariants(scope: Scope) {
        val type = getType(scope)

        if (expressions.size > 1 && type != BasicType(TerminalType.NUMBER))
            throw MultiplyError()

        checker.checkInvariants(type, expressions, scope)
    }

    override fun getType(scope: Scope): TypeObject = getFirstElement().getType(scope)

    fun getFirstElement(): PrimaryExpression = expressions.first().getFirstElement()

    private fun calculateNumber(acc: Float, nextValue: Float, operator: TerminalType): Float {
        return when (operator) {
            TerminalType.TIMES -> acc * nextValue
            TerminalType.DIVIDE -> acc / nextValue
            else -> throw ExpressionExecutionError(this, "Operator not allowed $operator")
        }
    }
}

data class ParenthesisExpression(val expression: Expression) : PrimaryExpression {
    override fun execute(scope: Scope): Production? = expression.execute(scope)
    override fun checkInvariants(scope: Scope) = expression.checkInvariants(scope)
    override fun getType(scope: Scope): TypeObject = expression.getType(scope)
}

interface PrimaryExpression : Production {
    fun getFirstElement(): PrimaryExpression =
        if (this is ParenthesisExpression) this.expression.getFirstElement() else this
}

class MultiplyError : Throwable("Multiplying variables that are not numbers")
class ExpressionTypeMismatch(expressionType: TypeObject, primaryExpression: TypeObject) :
    Throwable("One of the element of an expression was incorrect type. $expressionType expected got $primaryExpression")

class ExpressionExecutionError(executedExpression: Production, extraInfo: String = "") :
    ExecutionError(executedExpression, "Expression incorrect type in execution. $extraInfo")
