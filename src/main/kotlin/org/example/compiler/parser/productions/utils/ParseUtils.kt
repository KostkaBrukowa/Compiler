package org.example.compiler.parser.productions.utils

import org.example.compiler.lexer.TokenMeta
import org.example.compiler.lexer.token.TerminalToken
import org.example.compiler.lexer.token.TerminalType
import org.example.compiler.parser.BufferedLexer
import org.example.compiler.parser.Production
import org.example.compiler.parser.ProductionParser
import org.example.compiler.parser.utils.UnexpectedToken
import org.example.compiler.parser.utils.advance
import org.example.compiler.parser.utils.advanceAndRequireTerminals
import org.example.compiler.parser.utils.isIn

fun <P : Production> parseZeroOrMore(
    lexer: BufferedLexer,
    parser: ProductionParser<P>,
    delimiter: TerminalType? = null
): List<P> {
    val delimiters = if (delimiter != null) listOf(delimiter) else emptyList()

    return parseZeroOrMore(lexer, parser, delimiters).first
}

typealias  ProductionsWithOperators <P> = Pair<List<P>, List<TerminalType>>

fun <P : Production> parseZeroOrMore(
    lexer: BufferedLexer,
    parser: ProductionParser<P>,
    delimiters: List<TerminalType>
): ProductionsWithOperators<P> {

    fun parseWithDelimiter(
        currentProductions: List<P>,
        currentDelimiters: List<TerminalType> = emptyList()
    ): ProductionsWithOperators<P> {
        val delimiterToken = lexer.peekNextTokenMeta()?.token ?: return currentProductions to currentDelimiters

        if (delimiterToken is TerminalToken && delimiters.contains(delimiterToken.type)) {
            lexer.advance()

            if (!parser.matchesFirstToken(lexer.peekNextTokenMeta()?.token)) {
                throw UnexpectedToken(lexer.getCurrentTokenMeta())
            }

            return parseWithDelimiter(
                currentProductions + parser.parse(lexer),
                currentDelimiters + delimiterToken.type
            )
        }

        return currentProductions to currentDelimiters
    }

    fun parseWithoutDelimiter(currentFunctionDefs: List<P>): List<P> {
        val nextToken = lexer.peekNextTokenMeta() ?: return currentFunctionDefs

        if (parser.matchesFirstToken(nextToken.token)) {
            return parseWithoutDelimiter(currentFunctionDefs + parser.parse(lexer))
        }

        return currentFunctionDefs
    }

    val parsedProduction = parseZeroOrOne(lexer, parser) ?: return emptyList<P>() to emptyList()
    val currentFunctionDefs = listOf(parsedProduction)

    return if (delimiters.isEmpty())
        parseWithoutDelimiter(currentFunctionDefs) to emptyList()
    else
        parseWithDelimiter(currentFunctionDefs)
}

fun <P : Production> parseOneOrMore(
    lexer: BufferedLexer,
    parser: ProductionParser<P>,
    delimiter: TerminalType?
): List<P> {
    val delimiters = if (delimiter != null) listOf(delimiter) else emptyList()

    return parseOneOrMore(lexer, parser, delimiters).first
}

fun <P : Production> parseOneOrMore(
    lexer: BufferedLexer,
    parser: ProductionParser<P>,
    delimiter: List<TerminalType>
): ProductionsWithOperators<P> {
    return parseZeroOrMore(lexer, parser, delimiter).also {
        if (it.first.isEmpty()) throw NoElementsFound(lexer.getCurrentTokenMeta())
    }
}

fun <P : Production> parseZeroOrOne(lexer: BufferedLexer, parser: ProductionParser<P>): P? {
    val nextToken = lexer.peekNextTokenMeta()?.token

    if (!parser.matchesFirstToken(nextToken)) return null

    return parser.parse(lexer)
}

fun <P : Production> parseOneOrTwo(
    lexer: BufferedLexer,
    parser: ProductionParser<P>,
    delimiters: List<TerminalType>
): Triple<P, TerminalType?, P?> {
    val leftHandSide = parser.parse(lexer)

    val nextToken = lexer.peekNextTokenMeta()?.token

    if (!nextToken.isIn(delimiters)) {
        return Triple(leftHandSide, null, null)
    }

    val operator = lexer.advanceAndRequireTerminals(delimiters)

    val rightHandSide = parser.parse(lexer)

    return Triple(leftHandSide, operator.type, rightHandSide)
}

class NoElementsFound(currentToken: TokenMeta?) :
    UnexpectedToken(currentToken, null, "There supposed to be one or more, but none was there.")
