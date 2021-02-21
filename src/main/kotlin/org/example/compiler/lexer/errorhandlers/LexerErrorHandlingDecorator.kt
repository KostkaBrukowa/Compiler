package org.example.compiler.lexer.errorhandlers

import org.example.compiler.lexer.Position
import org.example.compiler.lexer.TokenMeta
import org.example.compiler.lexer.token.UnclosedStringToken
import org.example.compiler.lexer.token.UnrecognisedToken
import org.example.compiler.parser.Lexer

class LexerErrorHandlingDecorator(private val lexer: Lexer) : Lexer {
    override fun getNextTokenMeta(): TokenMeta? {
        val currentToken = lexer.getNextTokenMeta() ?: return null

        when (currentToken.token) {
            is UnrecognisedToken -> handleUnrecognisedTokenError(currentToken.token, currentToken.position)
            is UnclosedStringToken -> handleUnclosedStringToken(currentToken.token, currentToken.position)
        }

        return currentToken
    }

    private fun positionInfo(position: Position) =
        "In character at position: ${position.column} and row: ${position.row}"

    private fun handleUnclosedStringToken(token: UnclosedStringToken, position: Position) {
        println("${positionInfo(position)} there was unmatched string '${token.lexeme}'")
    }

    private fun handleUnrecognisedTokenError(token: UnrecognisedToken, position: Position) {
        println("In character at position: ${position.column} and row: ${position.row} there was unexpected token '${token.lexeme}'")
    }
}
