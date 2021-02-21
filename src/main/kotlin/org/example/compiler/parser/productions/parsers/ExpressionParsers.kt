package org.example.compiler.parser.productions.parsers

import org.example.compiler.lexer.token.IdToken
import org.example.compiler.lexer.token.TerminalType
import org.example.compiler.lexer.token.Token
import org.example.compiler.parser.BufferedLexer
import org.example.compiler.parser.ProductionParser
import org.example.compiler.parser.productions.*
import org.example.compiler.parser.productions.utils.OneOrMoreParser
import org.example.compiler.parser.productions.utils.OrParser
import org.example.compiler.parser.utils.advanceAndRequire
import org.example.compiler.parser.utils.isA

object ExpressionParser : OneOrMoreParser<MultiplicativeExpression, Expression> {
    override val availableOperators = listOf(TerminalType.PLUS, TerminalType.MINUS)
    override val parser =
        MultiplicativeExpressionParser
    override val produce = { expressions: List<MultiplicativeExpression>, operators: List<TerminalType> ->
        Expression(expressions, operators)
    }
}

object MultiplicativeExpressionParser : OneOrMoreParser<PrimaryExpression, MultiplicativeExpression> {
    override val availableOperators = listOf(TerminalType.TIMES, TerminalType.DIVIDE)
    override val parser = PrimaryExpressionParser
    override val produce = { expressions: List<PrimaryExpression>, operators: List<TerminalType> ->
        MultiplicativeExpression(expressions, operators)
    }
}

object ParenthesisExpressionParser : ProductionParser<ParenthesisExpression> {

    override fun parse(lexer: BufferedLexer): ParenthesisExpression {
        lexer.advanceAndRequire(TerminalType.OPEN_PAREN)

        val expression = ExpressionParser.parse(lexer)

        lexer.advanceAndRequire(TerminalType.CLOSE_PAREN)

        return ParenthesisExpression(expression)
    }

    override fun matchesFirstToken(token: Token?): Boolean = token.isA(TerminalType.OPEN_PAREN)
}

object PrimaryExpressionParser : OrParser<PrimaryExpression> {
    override val parsers: List<ProductionParser<out PrimaryExpression>> = listOf(
        LiteralParser,
        IdReferenceParser,
        ParenthesisExpressionParser,
        ListExpressionParser
    )

    override fun parse(lexer: BufferedLexer): PrimaryExpression {
        val token = lexer.peekNextTokenMeta(1)?.token

        if (token is IdToken) {
            if (ListModifierParser.matchesFirstToken(lexer.peekNextTokenMeta(2)?.token)) {
                return ListExpressionParser.parse(lexer)
            }

            return IdReferenceParser.parse(lexer)
        }

        return super.parse(lexer)
    }
}
