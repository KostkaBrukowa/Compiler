package org.example.compiler.parser.productions.parsers

import org.example.compiler.lexer.token.TerminalType
import org.example.compiler.lexer.token.Token
import org.example.compiler.parser.BufferedLexer
import org.example.compiler.parser.ProductionParser
import org.example.compiler.parser.productions.FunCall
import org.example.compiler.parser.productions.subproductions.AssignableParser
import org.example.compiler.parser.productions.utils.parseZeroOrMore
import org.example.compiler.parser.utils.advanceAndRequire
import org.example.compiler.parser.utils.isA

object FunCallParser :
    ProductionParser<FunCall> {

    override fun parse(lexer: BufferedLexer): FunCall {
        lexer.advanceAndRequire(TerminalType.OPEN_PAREN)

        val arguments = parseZeroOrMore(
            lexer,
            AssignableParser,
            TerminalType.COMMA
        )

        lexer.advanceAndRequire(TerminalType.CLOSE_PAREN)

        return FunCall(arguments)
    }

    override fun matchesFirstToken(token: Token?): Boolean = token.isA(
        TerminalType.OPEN_PAREN
    )
}
