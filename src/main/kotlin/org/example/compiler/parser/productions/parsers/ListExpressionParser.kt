package org.example.compiler.parser.productions.parsers

import org.example.compiler.lexer.token.Token
import org.example.compiler.parser.BufferedLexer
import org.example.compiler.parser.ProductionParser
import org.example.compiler.parser.productions.ListAsValueParser
import org.example.compiler.parser.productions.ListExpression
import org.example.compiler.parser.productions.utils.parseZeroOrMore

object ListExpressionParser : ProductionParser<ListExpression> {
    override fun parse(lexer: BufferedLexer): ListExpression {
        val listLiteral = ListAsValueParser.parse(lexer)

        val modifiers = parseZeroOrMore(lexer, ListModifierParser)

        return ListExpression(listLiteral, modifiers)
    }

    override fun matchesFirstToken(token: Token?): Boolean =
        ListProductionParser.matchesFirstToken(token)
}
