package org.example.compiler.lexer.recognisers

import org.example.compiler.lexer.token.IdToken
import org.example.compiler.lexer.token.Token
import org.example.compiler.lexer.token.isTerminalKeyword
import org.example.compiler.lexer.token.toTerminalToken
import org.example.compiler.source.Source

open class SingleWordRecogniser : TokenRecogniser {

    override fun recognise(source: Source): String? {

        fun tryToGetIdRec(currentString: String, offset: Int): String? {
            val currentChar = source.peekNextChar(offset)

            if (currentChar == null || !currentChar.isLetterOrDigit()) {
                return currentString
            }

            return tryToGetIdRec(currentString + currentChar, offset + 1)
        }

        val currentChar = source.getCurrentChar()

        return if (currentChar == null || !currentChar.isLetter()) null else tryToGetIdRec("$currentChar", 1)
    }

    override fun convertToToken(lexeme: String): Token = when {
        lexeme.isTerminalKeyword() -> lexeme.toTerminalToken()
        else -> IdToken(lexeme)
    }
}
