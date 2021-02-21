package org.example.compiler.lexer.token

data class NumberToken(val lexeme: Double) : Token(TokenType.NUMBER)
