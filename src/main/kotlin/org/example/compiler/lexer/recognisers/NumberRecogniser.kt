package org.example.compiler.lexer.recognisers

import org.example.compiler.lexer.token.NumberToken
import org.example.compiler.lexer.token.Token
import org.example.compiler.source.Source

open class NumberRecogniser : TokenRecogniser {
    companion object {
        const val MAX_NUMBER_SIZE = 18
    }

    private fun String.toStringNumberOrNull(): String? {
        return if (this.toIntOrNull() != null || this.toFloatOrNull() != null) this else null
    }

    override fun recognise(source: Source): String? {

        fun tryToGetNumber(currentString: String, offset: Int = 0, dotPresent: Boolean = false): String? {
            val currentChar = source.peekNextChar(offset)
                ?: return currentString.toStringNumberOrNull()

            if (currentString.length >= MAX_NUMBER_SIZE) {
                return null
            }

            if (currentChar == '.') {
                return if (dotPresent) currentString else tryToGetNumber("$currentString.", offset + 1, true)
            }

            if (!currentChar.isDigit()) {
                return currentString.toStringNumberOrNull()
            }

            return tryToGetNumber(currentString + currentChar, offset + 1, dotPresent)
        }

        return tryToGetNumber("")
    }

    override fun convertToToken(lexeme: String): Token = NumberToken(lexeme.toDouble())
}
