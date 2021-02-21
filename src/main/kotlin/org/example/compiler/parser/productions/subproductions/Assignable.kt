package org.example.compiler.parser.productions.subproductions

import org.example.compiler.lexer.token.IdToken
import org.example.compiler.lexer.token.TerminalType
import org.example.compiler.parser.BufferedLexer
import org.example.compiler.parser.Production
import org.example.compiler.parser.ProductionParser
import org.example.compiler.parser.productions.TrueFalseParser
import org.example.compiler.parser.productions.parsers.AnonymousFunctionParser
import org.example.compiler.parser.productions.parsers.ExpressionParser
import org.example.compiler.parser.productions.utils.OrParser
import org.example.compiler.parser.utils.isA

interface Assignable : Production

object AssignableParser : OrParser<Assignable> {
    override val parsers: List<ProductionParser<out Assignable>> = listOf(
        ExpressionParser,
        AnonymousFunctionParser,
        TrueFalseParser
    )

    override fun parse(lexer: BufferedLexer): Assignable {
        val maybeOpenParen = lexer.peekNextTokenMeta(1)

        if (maybeOpenParen?.token.isA(TerminalType.OPEN_PAREN)) {
            val maybeIdTokenOrCloseParenToken = lexer.peekNextTokenMeta(2)?.token

            if (maybeIdTokenOrCloseParenToken is IdToken || maybeIdTokenOrCloseParenToken.isA(TerminalType.CLOSE_PAREN)) {
                val maybeColonToken = lexer.peekNextTokenMeta(3)?.token

                if (maybeColonToken.isA(TerminalType.COLON)) {
                    return AnonymousFunctionParser.parse(lexer)
                }

                return ExpressionParser.parse(lexer)
            }
        }

        return super.parse(lexer)
    }
}


