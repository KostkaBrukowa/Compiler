package org.example.compiler.parser.productions.parsers

import org.example.compiler.lexer.token.TerminalType
import org.example.compiler.lexer.token.Token
import org.example.compiler.parser.BufferedLexer
import org.example.compiler.parser.ProductionParser
import org.example.compiler.parser.productions.ListModifier
import org.example.compiler.parser.productions.ListModifierCallback
import org.example.compiler.parser.utils.advanceAndRequire
import org.example.compiler.parser.utils.advanceAndRequireTerminals
import org.example.compiler.parser.utils.isA

object ListModifierParser :
    ProductionParser<ListModifier> {
    private val listModifiers = listOf(
        TerminalType.MAP,
        TerminalType.FILTER,
        TerminalType.CONCAT
    )

    override fun parse(lexer: BufferedLexer): ListModifier {
        lexer.advanceAndRequire(TerminalType.DOT)

        val modifier = lexer.advanceAndRequireTerminals(listModifiers)

        lexer.advanceAndRequire(TerminalType.OPEN_PAREN)

        val callback = ListModifierCallback.Parser.parse(lexer)

        lexer.advanceAndRequire(TerminalType.CLOSE_PAREN)

        return ListModifier(modifier.type, callback)
    }

    override fun matchesFirstToken(token: Token?): Boolean = token.isA(
        TerminalType.DOT
    )
}
