package org.example.compiler.parser.productions.parsers

import org.example.compiler.lexer.token.IdToken
import org.example.compiler.lexer.token.TerminalType
import org.example.compiler.lexer.token.Token
import org.example.compiler.parser.BufferedLexer
import org.example.compiler.parser.ProductionParser
import org.example.compiler.parser.productions.ArgumentWithType
import org.example.compiler.parser.productions.subproductions.TypeObjectParser
import org.example.compiler.parser.utils.advanceAndRequire

object ArgumentWithTypeParser :
    ProductionParser<ArgumentWithType> {
    override fun parse(lexer: BufferedLexer): ArgumentWithType {
        val idReference =
            IdReferenceParser.parseOnlyId(lexer)

        lexer.advanceAndRequire(TerminalType.COLON, "Type not specified in init statement")

        val type = TypeObjectParser.parse(lexer)

        return ArgumentWithType(idReference.id, type)
    }

    override fun matchesFirstToken(token: Token?): Boolean = token is IdToken

}
