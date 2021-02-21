package org.example.compiler.parser.productions

import org.example.compiler.parser.Scope
import org.example.compiler.lexer.token.StringToken
import org.example.compiler.lexer.token.TerminalType
import org.example.compiler.lexer.token.Token
import org.example.compiler.parser.BufferedLexer
import org.example.compiler.parser.Production
import org.example.compiler.parser.ProductionParser
import org.example.compiler.parser.productions.subproductions.TypeObject
import org.example.compiler.parser.utils.advanceAndGetToken

data class StringLiteral(val value: String) : Literal {
    override fun execute(scope: Scope): Production = this
    override fun getType(scope: Scope): TypeObject = BasicType(TerminalType.STRING)
}

object StringLiteralParser : ProductionParser<StringLiteral> {

    override fun parse(lexer: BufferedLexer) = StringLiteral(lexer.advanceAndGetToken<StringToken>().lexeme)

    override fun matchesFirstToken(token: Token?): Boolean = token is StringToken
}

