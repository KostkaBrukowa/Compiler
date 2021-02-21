package org.example.compiler.parser.productions.parsers

import org.example.compiler.lexer.token.TerminalType
import org.example.compiler.lexer.token.Token
import org.example.compiler.parser.BufferedLexer
import org.example.compiler.parser.ProductionParser
import org.example.compiler.parser.productions.BasicType
import org.example.compiler.parser.utils.advanceAndRequireTerminals
import org.example.compiler.parser.utils.isIn

object BasicTypeParser : ProductionParser<BasicType> {
    private val availableTypes =
        listOf(
            TerminalType.BOOLEAN,
            TerminalType.LIST,
            TerminalType.NUMBER,
            TerminalType.STRING
        )

    override fun parse(lexer: BufferedLexer): BasicType {
        val typeToken = lexer.advanceAndRequireTerminals(availableTypes, "Invalid type specified")

        return BasicType(typeToken.type)
    }

    override fun matchesFirstToken(token: Token?): Boolean = token.isIn(availableTypes)

}
