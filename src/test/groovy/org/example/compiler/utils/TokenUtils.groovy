package org.example.compiler.utils

import org.example.compiler.lexer.Position
import org.example.compiler.lexer.TokenMeta
import org.example.compiler.lexer.token.*

class TokenUtils {
    static def t(Object type, String lexeme = "") {
        def token = token(type, lexeme)

        if (token == null) return null

        return new TokenMeta(token, new Position(0, 0))
    }

    static def token(Object type, String lexeme = "") {
        if (lexeme == null) return null

        if (type instanceof TerminalType) return new TerminalToken(type)
        if (type instanceof TokenType) {
            if (type == TokenType.ID) return new IdToken(lexeme)
            if (type == TokenType.NUMBER) return new NumberToken(lexeme.toDouble())
            if (type == TokenType.STRING) return new StringToken(lexeme)
            if (type == TokenType.UNCLOSED_STRING) return new UnclosedStringToken(lexeme)
            if (type == TokenType.UNRECOGNISED_TOKEN) return new UnrecognisedToken(lexeme)
        }

        return null
    }
}
