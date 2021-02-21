package org.example.compiler.lexer.recognisers

import org.example.compiler.lexer.token.StringToken
import org.example.compiler.lexer.token.Token
import org.example.compiler.lexer.token.UnclosedStringToken
import org.example.compiler.source.Source
import org.example.compiler.utils.isEndline

open class StringRecogniser : TokenRecogniser {
    override fun recognise(source: Source): String? {

        fun tryToGetString(currentString: String, offset: Int = 0): String? {
            val currentChar = source.peekNextChar(offset)
                ?: return currentString
            val newString = currentString + currentChar

            if (currentChar.isEndline()) {
                return currentString
            }

            if (currentChar == '"') {
                return newString
            }

            return tryToGetString(newString, offset + 1)
        }

        val currentChar = source.getCurrentChar() ?: return null

        return if (currentChar == '"') tryToGetString("\"", 1) else null
    }

    override fun convertToToken(lexeme: String): Token {
        if (!lexeme.endsWith('"')) return UnclosedStringToken(lexeme.substring(1))

        return StringToken(lexeme.substring(1, lexeme.length - 1))
    }
}
