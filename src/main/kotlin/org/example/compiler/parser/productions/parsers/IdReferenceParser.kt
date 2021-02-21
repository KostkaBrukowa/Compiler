package org.example.compiler.parser.productions.parsers

import org.example.compiler.lexer.token.IdToken
import org.example.compiler.lexer.token.Token
import org.example.compiler.parser.BufferedLexer
import org.example.compiler.parser.ProductionParser
import org.example.compiler.parser.productions.IdReference
import org.example.compiler.parser.productions.IdReferenceArgumentParser
import org.example.compiler.parser.productions.utils.parseZeroOrOne
import org.example.compiler.parser.utils.advanceAndGetToken

object IdReferenceParser : ProductionParser<IdReference> {

    override fun parse(lexer: BufferedLexer): IdReference {
        val id = lexer.advanceAndGetToken<IdToken>()

        val argument = parseZeroOrOne(
            lexer,
            IdReferenceArgumentParser
        )

        return IdReference(id.lexeme, argument)
    }

    fun parseOnlyId(lexer: BufferedLexer): IdReference {
        val id = lexer.advanceAndGetToken<IdToken>()

        return IdReference(id.lexeme, null)
    }

    override fun matchesFirstToken(token: Token?): Boolean = token is IdToken
}
