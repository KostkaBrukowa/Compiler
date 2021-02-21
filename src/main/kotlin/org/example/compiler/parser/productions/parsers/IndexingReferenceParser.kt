package org.example.compiler.parser.productions.parsers

import org.example.compiler.lexer.token.TerminalType
import org.example.compiler.lexer.token.Token
import org.example.compiler.parser.BufferedLexer
import org.example.compiler.parser.ProductionParser
import org.example.compiler.parser.productions.IndexingReference
import org.example.compiler.parser.utils.advanceAndRequire
import org.example.compiler.parser.utils.isA

object IndexingReferenceParser :
    ProductionParser<IndexingReference> {

    override fun parse(lexer: BufferedLexer): IndexingReference {
        lexer.advanceAndRequire(TerminalType.OPEN_SQUARE_BRACKET)

        val expression = ExpressionParser.parse(lexer)

        lexer.advanceAndRequire(TerminalType.CLOSE_SQUARE_BRACKET)

        return IndexingReference(expression)
    }

    override fun matchesFirstToken(token: Token?): Boolean = token.isA(
        TerminalType.OPEN_SQUARE_BRACKET
    )
}
