package org.example.compiler.parser.productions

import org.example.compiler.parser.Scope
import org.example.compiler.lexer.token.TerminalType
import org.example.compiler.parser.Production
import org.example.compiler.parser.productions.subproductions.Assignable
import org.example.compiler.parser.productions.subproductions.TypeObject

fun getBooleanFromCondition(production: Production, scope: Scope): Boolean {
    val conditionResult = production.execute(scope) ?: throw NullFromNotNullableProduction(production)
    if (conditionResult !is TrueFalse)
        throw NonBooleanFromConditionError(conditionResult)

    return conditionResult.isTrue
}

data class Condition(val andConditions: List<AndCondition>) : Assignable {
    override fun execute(scope: Scope): TrueFalse = TrueFalse(andConditions.any { getBooleanFromCondition(it, scope) })

    override fun getType(scope: Scope): TypeObject =
        if (andConditions.size > 1) BasicType(TerminalType.BOOLEAN) else andConditions.first().getType(scope)

    override fun checkInvariants(scope: Scope) {
        andConditions.forEach { andCondition ->
            andCondition.checkInvariants(scope)
        }
    }
}


data class AndCondition(val equalityConditions: List<EqualityCondition>) : Production {
    override fun execute(scope: Scope): Production =
        TrueFalse(equalityConditions.all { getBooleanFromCondition(it, scope) })

    override fun getType(scope: Scope): TypeObject =
        if (equalityConditions.size > 1) BasicType(TerminalType.BOOLEAN) else equalityConditions.first().getType(scope)

    override fun checkInvariants(scope: Scope) {
        equalityConditions.forEach { equalityCondition ->
            equalityCondition.checkInvariants(scope)
        }
    }
}

data class EqualityCondition(
    val leftHandSideCondition: RelationalCondition,
    val rightHandSideCondition: RelationalCondition? = null,
    val operator: TerminalType? = null
) : Production {
    override fun execute(scope: Scope): Production? {
        val leftHandSideResult = leftHandSideCondition.execute(scope)
        val rightHandSideResult = rightHandSideCondition?.execute(scope) ?: return leftHandSideResult

        val comparisonResult = when (leftHandSideResult) {
            is NumberLiteral -> leftHandSideResult.value == (rightHandSideResult as NumberLiteral).value
            is StringLiteral -> leftHandSideResult.value == (rightHandSideResult as StringLiteral).value
            is TrueFalse -> leftHandSideResult.isTrue == (rightHandSideResult as TrueFalse).isTrue
            else -> throw UnsupportedConditionOperation(leftHandSideResult, this)
        }

        return if (operator == TerminalType.EQUALS_EQUALS) TrueFalse(comparisonResult) else TrueFalse(!comparisonResult)
    }

    override fun getType(scope: Scope): TypeObject =
        if (rightHandSideCondition != null) BasicType(TerminalType.BOOLEAN) else leftHandSideCondition.getType(scope)

    override fun checkInvariants(scope: Scope) {
        leftHandSideCondition.checkInvariants(scope)
        rightHandSideCondition?.checkInvariants(scope)

        val leftHandSideType = leftHandSideCondition.getType(scope)
        val rightHandSideType = rightHandSideCondition?.getType(scope) ?: return

        if (leftHandSideType != rightHandSideType)
            throw ConditionTypeMismatch(leftHandSideType, rightHandSideType)
    }
}


data class RelationalCondition(
    val leftHandSideCondition: PrimaryCondition,
    val rightHandSideCondition: PrimaryCondition? = null,
    val operator: TerminalType? = null
) : Production {
    override fun execute(scope: Scope): Production? {
        val leftHandSideResult = leftHandSideCondition.execute(scope)
        val rightHandSideResult = rightHandSideCondition?.execute(scope) ?: return leftHandSideResult

        return TrueFalse(
            when (leftHandSideResult) {
                is NumberLiteral -> compareNumbers(leftHandSideResult, rightHandSideResult as NumberLiteral)
                else -> throw UnsupportedConditionOperation(leftHandSideResult, this)
            }
        )
    }

    private fun compareNumbers(leftHandSideResult: NumberLiteral, rightHandSideResult: NumberLiteral): Boolean =
        when (operator) {
            TerminalType.LESS_THAN -> leftHandSideResult.value < rightHandSideResult.value
            TerminalType.GREATER_THAN -> leftHandSideResult.value > rightHandSideResult.value
            TerminalType.LESS_EQUALS_THAN -> leftHandSideResult.value <= rightHandSideResult.value
            TerminalType.GREATER_EQUALS_THAN -> leftHandSideResult.value >= rightHandSideResult.value
            else -> throw UnsupportedConditionOperation(leftHandSideResult, this)
        }

    override fun getType(scope: Scope): TypeObject =
        if (rightHandSideCondition != null) BasicType(TerminalType.BOOLEAN) else leftHandSideCondition.getType(scope)

    override fun checkInvariants(scope: Scope) {
        leftHandSideCondition.checkInvariants(scope)
        rightHandSideCondition?.checkInvariants(scope)

        val leftHandSideType = leftHandSideCondition.getType(scope)
        val rightHandSideType = rightHandSideCondition?.getType(scope) ?: return

        if (rightHandSideType != BasicType(TerminalType.NUMBER) || leftHandSideType != BasicType(TerminalType.NUMBER))
            throw ComparisonOfNonNumbersError()
    }
}


interface ConditionElement : Production

data class PrimaryCondition(val negated: Boolean, val conditionElement: ConditionElement) : Production {
    override fun execute(scope: Scope): Production? {
        val conditionResult = conditionElement.execute(scope) ?: throw NullFromNotNullableProduction(conditionElement)

        if (negated) {
            return TrueFalse(!(conditionResult as TrueFalse).isTrue)
        }

        return conditionResult
    }

    override fun checkInvariants(scope: Scope) {
        if (conditionElement is IdReference) {
            checkIdReferenceType(conditionElement, scope)
        } else if (conditionElement is ParenthesisCondition) {
            conditionElement.condition.checkInvariants(scope)
        }
    }

    override fun getType(scope: Scope): TypeObject = conditionElement.getType(scope)

    private fun checkIdReferenceType(idReference: IdReference, scope: Scope) {
        val idReferenceType = idReference.getType(scope)

        if (idReferenceType != BasicType(TerminalType.NUMBER)
            && idReferenceType != BasicType(TerminalType.BOOLEAN)
            && idReferenceType != BasicType(TerminalType.STRING)
        ) {
            throw TypeError("Id $idReference was not a number nor boolean nor string")
        }

        if (idReferenceType != BasicType(TerminalType.BOOLEAN) && negated)
            throw UnsupportedConditionOperation(idReferenceType, this)
    }
}

data class ParenthesisCondition(val condition: Condition) : ConditionElement {
    override fun execute(scope: Scope): Production? = condition.execute(scope)
    override fun getType(scope: Scope): TypeObject = condition.getType(scope)
    override fun checkInvariants(scope: Scope) = condition.checkInvariants(scope)
}

class ConditionTypeMismatch(
    leftHandSideType: TypeObject,
    rightHandSideType: TypeObject?
) : TypeError("Type of the two sides of the condition was not equal. Got $leftHandSideType, $rightHandSideType")

class NonBooleanFromConditionError(production: Production) : TypeError("Condition was not a boolean. $production")

class ComparisonOfNonNumbersError : TypeError("Trying to compare two elements that are not both numbers")
class UnsupportedConditionOperation(condition: Production?, sourceCondition: Production) :
    Throwable("Unsupported condition operation. Type $condition $sourceCondition")
