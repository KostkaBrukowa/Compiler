package org.example.compiler.parser.productions.parsers

import org.example.compiler.lexer.token.TerminalType
import org.example.compiler.lexer.token.Token
import org.example.compiler.parser.BufferedLexer
import org.example.compiler.parser.ProductionParser
import org.example.compiler.parser.productions.InitStatement
import org.example.compiler.parser.productions.subproductions.AssignableParser
import org.example.compiler.parser.utils.advanceAndRequire
import org.example.compiler.parser.utils.isA

object InitStatementParser : ProductionParser<InitStatement> {

    override fun parse(lexer: BufferedLexer): InitStatement {
        lexer.advanceAndRequire(TerminalType.VAL)

        val argWithType =
            ArgumentWithTypeParser.parse(lexer)

        lexer.advanceAndRequire(TerminalType.EQUALS)

        val assignable =
            AssignableParser.parse(lexer)

        lexer.advanceAndRequire(TerminalType.SEMICOLON)

        return InitStatement(
            argWithType.id,
            argWithType,
            assignable
        )
    }

    override fun matchesFirstToken(token: Token?): Boolean = token.isA(
        TerminalType.VAL
    )
}
