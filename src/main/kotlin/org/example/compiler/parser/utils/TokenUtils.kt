package org.example.compiler.parser.utils

import org.example.compiler.lexer.TokenMeta
import org.example.compiler.lexer.token.TerminalToken
import org.example.compiler.lexer.token.TerminalType
import org.example.compiler.lexer.token.Token
import org.example.compiler.parser.BufferedLexer

inline fun <reified T> BufferedLexer.advanceAndGetToken(): T {
    val tokenMeta = getNextTokenMeta() ?: throw NoMoreTokensDuringParseException()

    if (tokenMeta.token !is T)
        throw UnexpectedToken(tokenMeta)

    return tokenMeta.token
}

fun BufferedLexer.advance() {
    getNextTokenMeta()
}

fun BufferedLexer.advanceAndRequireTerminals(expectedTypes: List<TerminalType>, extraInfo: String = ""): TerminalToken {
    val tokenMeta = getNextTokenMeta() ?: throw NoMoreTokensDuringParseException()

    if (tokenMeta.token !is TerminalToken || expectedTypes.all { tokenMeta.token.type != it })
        throw UnexpectedToken(tokenMeta, "$expectedTypes $extraInfo")

    return tokenMeta.token
}

fun BufferedLexer.advanceAndRequire(expectedType: TerminalType, extraInfo: String = ""): TerminalToken =
    advanceAndRequireTerminals(listOf(expectedType), extraInfo)


fun Token?.isA(terminalType: TerminalType) = this is TerminalToken && this.type == terminalType
fun Token?.isIn(terminalTypes: List<TerminalType>) = this is TerminalToken && terminalTypes.contains(this.type)

class NoMoreTokensDuringParseException : RuntimeException("Unexpected end of stream")

open class UnexpectedToken(currentToken: TokenMeta?, expectedType: String? = null, extraInfo: String = "") :
    RuntimeException("There was an unexpected token ${currentToken?.token} at position ${currentToken?.position}. Expected type was $expectedType. $extraInfo")
