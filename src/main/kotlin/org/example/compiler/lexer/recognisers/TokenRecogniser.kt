package org.example.compiler.lexer.recognisers

import org.example.compiler.lexer.token.Token
import org.example.compiler.source.Source

interface TokenRecogniser {
    fun recognise(source: Source): String?

    fun convertToToken(lexeme: String): Token
}
