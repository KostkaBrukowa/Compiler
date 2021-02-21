package org.example.compiler.parser.productions.parsers

import org.example.compiler.lexer.token.TerminalType
import org.example.compiler.lexer.token.Token
import org.example.compiler.parser.BufferedLexer
import org.example.compiler.parser.ProductionParser
import org.example.compiler.parser.productions.*
import org.example.compiler.parser.productions.utils.OneOrMoreParser
import org.example.compiler.parser.productions.utils.OneOrTwoParser
import org.example.compiler.parser.productions.utils.OrParser
import org.example.compiler.parser.utils.advanceAndRequire
import org.example.compiler.parser.utils.isA

object ConditionParser : OneOrMoreParser<AndCondition, Condition> {
    override val availableOperators = listOf(TerminalType.OR)
    override val parser = AndConditionParser
    override val produce = { andConditions: List<AndCondition>, _: Any ->
        Condition(andConditions)
    }
}

object AndConditionParser : OneOrMoreParser<EqualityCondition, AndCondition> {
    override val availableOperators = listOf(TerminalType.AND)
    override val parser = EqualityConditionParser
    override val produce = { equalityConditions: List<EqualityCondition>, _: Any ->
        AndCondition(equalityConditions)
    }
}

object EqualityConditionParser : OneOrTwoParser<RelationalCondition, EqualityCondition> {
    override val availableOperators = listOf(TerminalType.EQUALS_EQUALS, TerminalType.DOES_NOT_EQUALS)
    override val parser = RelationalConditionParser
    override val produce = { left: RelationalCondition, operator: TerminalType?, right: RelationalCondition? ->
        EqualityCondition(left, right, operator)
    }
}

object RelationalConditionParser : OneOrTwoParser<PrimaryCondition, RelationalCondition> {
    override val availableOperators = listOf(
        TerminalType.LESS_THAN,
        TerminalType.LESS_EQUALS_THAN,
        TerminalType.GREATER_THAN,
        TerminalType.GREATER_EQUALS_THAN
    )
    override val parser = PrimaryConditionParser
    override val produce = { left: PrimaryCondition, operator: TerminalType?, right: PrimaryCondition? ->
        RelationalCondition(left, right, operator)
    }
}


object ConditionElementParser : OrParser<ConditionElement> {
    override val parsers: List<ProductionParser<out ConditionElement>> = listOf(
        ParenthesisConditionParser,
        TrueFalseParser,
        IdReferenceParser,
        NumberLiteralParser,
        StringLiteralParser
    )
}


object PrimaryConditionParser : ProductionParser<PrimaryCondition> {

    override fun parse(lexer: BufferedLexer): PrimaryCondition {
        val negated = lexer.peekNextTokenMeta()
            ?.token.isA(TerminalType.NEGATION)
            .also { if (it) lexer.getNextTokenMeta() }

        val conditionElement = ConditionElementParser.parse(lexer)

        return PrimaryCondition(negated, conditionElement)
    }

    override fun matchesFirstToken(token: Token?): Boolean =
        token.isA(TerminalType.NEGATION) || ConditionElementParser.matchesFirstToken(token)
}


object ParenthesisConditionParser : ProductionParser<ParenthesisCondition> {

    override fun parse(lexer: BufferedLexer): ParenthesisCondition {
        lexer.advanceAndRequire(TerminalType.OPEN_PAREN)

        val condition = ConditionParser.parse(lexer)

        lexer.advanceAndRequire(TerminalType.CLOSE_PAREN)

        return ParenthesisCondition(condition)
    }

    override fun matchesFirstToken(token: Token?): Boolean = token.isA(TerminalType.OPEN_PAREN)
}
