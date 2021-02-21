package org.example.compiler.parser.productions.parsers

import org.example.compiler.lexer.token.TerminalType
import org.example.compiler.lexer.token.Token
import org.example.compiler.parser.BufferedLexer
import org.example.compiler.parser.ProductionParser
import org.example.compiler.parser.productions.StatementBlock
import org.example.compiler.parser.productions.StatementParser
import org.example.compiler.parser.productions.utils.parseZeroOrMore
import org.example.compiler.parser.utils.advanceAndRequire
import org.example.compiler.parser.utils.isA

object StatementBlockParser :
    ProductionParser<StatementBlock> {

    override fun parse(lexer: BufferedLexer): StatementBlock {
        lexer.advanceAndRequire(TerminalType.OPEN_BRACKET)

        val statements = parseZeroOrMore(
            lexer,
            StatementParser
        )

        lexer.advanceAndRequire(TerminalType.CLOSE_BRACKET)

        return StatementBlock(statements)
    }

    override fun matchesFirstToken(token: Token?): Boolean = token.isA(
        TerminalType.OPEN_BRACKET
    )
}
