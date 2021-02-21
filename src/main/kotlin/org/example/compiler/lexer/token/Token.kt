package org.example.compiler.lexer.token

open class Token(val id: TokenType)

enum class TokenType {
    NUMBER,
    STRING,
    TERMINAL,
    ID,
    UNCLOSED_STRING,
    UNRECOGNISED_TOKEN
}
