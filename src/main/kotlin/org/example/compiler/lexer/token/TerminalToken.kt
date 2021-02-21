package org.example.compiler.lexer.token

data class TerminalToken(val type: TerminalType) : Token(TokenType.TERMINAL) {
    companion object {
        val specialSet: Map<String, TerminalType> = mapOf(
            ">" to TerminalType.GREATER_THAN,
            ">=" to TerminalType.GREATER_EQUALS_THAN,
            "<" to TerminalType.LESS_THAN,
            "<=" to TerminalType.LESS_EQUALS_THAN,
            "=" to TerminalType.EQUALS,
            "!" to TerminalType.NEGATION,
            "{" to TerminalType.OPEN_BRACKET,
            "}" to TerminalType.CLOSE_BRACKET,
            "[" to TerminalType.OPEN_SQUARE_BRACKET,
            "]" to TerminalType.CLOSE_SQUARE_BRACKET,
            ":" to TerminalType.COLON,
            "(" to TerminalType.OPEN_PAREN,
            ")" to TerminalType.CLOSE_PAREN,
            "," to TerminalType.COMMA,
            "=>" to TerminalType.ARROW,
            ";" to TerminalType.SEMICOLON,
            "==" to TerminalType.EQUALS_EQUALS,
            "!=" to TerminalType.DOES_NOT_EQUALS,
            "^" to TerminalType.CARROT,
            "+" to TerminalType.PLUS,
            "-" to TerminalType.MINUS,
            "*" to TerminalType.TIMES,
            "/" to TerminalType.DIVIDE,
            "&&" to TerminalType.AND,
            "||" to TerminalType.OR,
            "%" to TerminalType.PERCENT,
            "fun" to TerminalType.FUN,
            "map" to TerminalType.MAP,
            "filter" to TerminalType.FILTER,
            "concat" to TerminalType.CONCAT,
            "val" to TerminalType.VAL,
            "return" to TerminalType.RETURN,
            "if" to TerminalType.IF,
            "else" to TerminalType.ELSE,
            "false" to TerminalType.FALSE,
            "true" to TerminalType.TRUE,
            "String" to TerminalType.STRING,
            "Number" to TerminalType.NUMBER,
            "List" to TerminalType.LIST,
            "Function" to TerminalType.FUNCTION,
            "Boolean" to TerminalType.BOOLEAN,
            "Arg" to TerminalType.ARG,
            "Returns" to TerminalType.RETURNS,
            "." to TerminalType.DOT
        )
    }
}

enum class TerminalType {
    GREATER_THAN,
    GREATER_EQUALS_THAN,
    LESS_THAN,
    LESS_EQUALS_THAN,
    EQUALS,
    NEGATION,
    OPEN_BRACKET,
    CLOSE_BRACKET,
    OPEN_SQUARE_BRACKET,
    CLOSE_SQUARE_BRACKET,
    COLON,
    OPEN_PAREN,
    CLOSE_PAREN,
    COMMA,
    ARROW,
    SEMICOLON,
    EQUALS_EQUALS,
    DOES_NOT_EQUALS,
    CARROT,
    PLUS,
    MINUS,
    TIMES,
    DIVIDE,
    AND,
    OR,
    PERCENT,

    FUN,
    MAP,
    FILTER,
    CONCAT,
    VAL,
    RETURN,
    IF,
    ELSE,
    FALSE,
    TRUE,

    NUMBER,
    STRING,
    LIST,
    FUNCTION,
    BOOLEAN,
    ARG,
    RETURNS,
    UNKNOWN_TYPE,
    DOT
}

fun String.isTerminalKeyword(): Boolean = TerminalToken.specialSet.containsKey(this)

fun String.toTerminalToken(): TerminalToken {
    val type = TerminalToken.specialSet[this]
        ?: throw InternalError("Terminal token was not recognized")

    return TerminalToken(type)
}


