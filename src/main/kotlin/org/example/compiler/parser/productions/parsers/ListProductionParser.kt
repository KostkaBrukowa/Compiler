package org.example.compiler.parser.productions.parsers

import org.example.compiler.lexer.token.TerminalType
import org.example.compiler.lexer.token.Token
import org.example.compiler.parser.BufferedLexer
import org.example.compiler.parser.ProductionParser
import org.example.compiler.parser.productions.ListProduction
import org.example.compiler.parser.productions.utils.parseZeroOrMore
import org.example.compiler.parser.utils.advanceAndRequire
import org.example.compiler.parser.utils.isA

object ListProductionParser : ProductionParser<ListProduction> {

    override fun parse(lexer: BufferedLexer): ListProduction {
        lexer.advanceAndRequire(TerminalType.OPEN_SQUARE_BRACKET)

        val expressions = parseZeroOrMore(
            lexer,
            ExpressionParser,
            TerminalType.COMMA
        )

        lexer.advanceAndRequire(TerminalType.CLOSE_SQUARE_BRACKET)

        return ListProduction(expressions)
    }

    override fun matchesFirstToken(token: Token?): Boolean = token.isA(
        TerminalType.OPEN_SQUARE_BRACKET
    )
}
