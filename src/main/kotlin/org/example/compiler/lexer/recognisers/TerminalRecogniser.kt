package org.example.compiler.lexer.recognisers

import org.example.compiler.lexer.token.Token
import org.example.compiler.lexer.token.isTerminalKeyword
import org.example.compiler.lexer.token.toTerminalToken
import org.example.compiler.source.Source

open class TerminalRecogniser : TokenRecogniser {
    override fun recognise(source: Source): String? {
        val firstChar = source.getCurrentChar()?.toString() ?: return null
        val secondChar = source.peekNextChar(1) ?: ""

        val joinedChars = firstChar + secondChar

        return when {
            joinedChars.isTerminalKeyword() -> joinedChars
            firstChar.isTerminalKeyword() -> firstChar
            else -> null
        }
    }

    override fun convertToToken(lexeme: String): Token = lexeme.toTerminalToken()
}
