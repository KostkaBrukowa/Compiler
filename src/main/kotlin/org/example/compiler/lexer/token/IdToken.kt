package org.example.compiler.lexer.token

data class IdToken(val lexeme: String): Token(TokenType.ID)
