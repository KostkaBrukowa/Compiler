package org.example.compiler.parser.productions

import org.example.compiler.parser.Scope
import org.example.compiler.lexer.token.NumberToken
import org.example.compiler.lexer.token.TerminalType
import org.example.compiler.lexer.token.Token
import org.example.compiler.parser.BufferedLexer
import org.example.compiler.parser.Production
import org.example.compiler.parser.ProductionParser
import org.example.compiler.parser.productions.subproductions.TypeObject
import org.example.compiler.parser.utils.advanceAndGetToken


data class NumberLiteral(val value: Float) : Literal {
    override fun execute(scope: Scope): Production = this
    override fun getType(scope: Scope): TypeObject = BasicType(TerminalType.NUMBER)
}

object NumberLiteralParser : ProductionParser<NumberLiteral> {

    override fun parse(lexer: BufferedLexer): NumberLiteral {
        val number = lexer.advanceAndGetToken<NumberToken>()

        return NumberLiteral(number.lexeme.toFloat())
    }

    override fun matchesFirstToken(token: Token?): Boolean = token is NumberToken
}
