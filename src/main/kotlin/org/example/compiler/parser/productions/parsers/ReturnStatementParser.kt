package org.example.compiler.parser.productions.parsers

import org.example.compiler.lexer.token.TerminalType
import org.example.compiler.lexer.token.Token
import org.example.compiler.parser.BufferedLexer
import org.example.compiler.parser.ProductionParser
import org.example.compiler.parser.productions.ReturnStatement
import org.example.compiler.parser.productions.subproductions.AssignableParser
import org.example.compiler.parser.utils.advanceAndRequire
import org.example.compiler.parser.utils.isA

object ReturnStatementParser : ProductionParser<ReturnStatement> {

    override fun parse(lexer: BufferedLexer): ReturnStatement {
        lexer.advanceAndRequire(TerminalType.RETURN)

        val assignable =
            AssignableParser.parse(lexer)

        lexer.advanceAndRequire(TerminalType.SEMICOLON)

        return ReturnStatement(assignable)
    }

    override fun matchesFirstToken(token: Token?): Boolean = token.isA(
        TerminalType.RETURN
    )

}
