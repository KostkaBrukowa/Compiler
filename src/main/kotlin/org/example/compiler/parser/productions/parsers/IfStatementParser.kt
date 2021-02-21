package org.example.compiler.parser.productions.parsers

import org.example.compiler.lexer.token.TerminalType
import org.example.compiler.lexer.token.Token
import org.example.compiler.parser.BufferedLexer
import org.example.compiler.parser.ProductionParser
import org.example.compiler.parser.productions.IfStatement
import org.example.compiler.parser.productions.StatementParser
import org.example.compiler.parser.utils.advance
import org.example.compiler.parser.utils.advanceAndRequire
import org.example.compiler.parser.utils.isA

object IfStatementParser : ProductionParser<IfStatement> {

    override fun parse(lexer: BufferedLexer): IfStatement {
        lexer.advanceAndRequire(TerminalType.IF)
        lexer.advanceAndRequire(TerminalType.OPEN_PAREN)

        val condition = ConditionParser.parse(lexer)

        lexer.advanceAndRequire(TerminalType.CLOSE_PAREN)

        val statement = StatementParser.parse(lexer)

        if (!lexer.peekNextTokenMeta()?.token.isA(TerminalType.ELSE)) {
            return IfStatement(condition, statement)
        }

        lexer.advance()

        val elseStatement = StatementParser.parse(lexer)

        return IfStatement(
            condition,
            statement,
            elseStatement
        )
    }

    override fun matchesFirstToken(token: Token?): Boolean = token.isA(
        TerminalType.IF
    )

}
