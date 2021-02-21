package org.example.compiler.lexer.token

data class StringToken(val lexeme: String) : Token(TokenType.STRING)

data class UnclosedStringToken(val lexeme: String) : Token(TokenType.UNCLOSED_STRING)
