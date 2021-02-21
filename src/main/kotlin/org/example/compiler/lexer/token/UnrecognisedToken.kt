package org.example.compiler.lexer.token

data class UnrecognisedToken(val lexeme: String) : Token(TokenType.UNRECOGNISED_TOKEN)
