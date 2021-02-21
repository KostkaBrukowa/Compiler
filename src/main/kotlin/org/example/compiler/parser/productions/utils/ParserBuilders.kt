package org.example.compiler.parser.productions.utils

import org.example.compiler.lexer.token.TerminalType
import org.example.compiler.lexer.token.Token
import org.example.compiler.parser.BufferedLexer
import org.example.compiler.parser.Production
import org.example.compiler.parser.ProductionParser
import org.example.compiler.parser.utils.UnexpectedToken

interface OrParser<T : Production> : ProductionParser<T> {
    val parsers: List<ProductionParser<out T>>

    override fun parse(lexer: BufferedLexer): T {
        val nextTokenMeta = lexer.peekNextTokenMeta()
        val matchingParser = parsers.firstOrNull { it.matchesFirstToken(nextTokenMeta?.token) }
            ?: throw UnexpectedToken(nextTokenMeta)

        return matchingParser.parse(lexer)
    }

    override fun matchesFirstToken(token: Token?) = parsers.any { it.matchesFirstToken(token) }
}

interface OneOrMoreParser<TypesToParse : Production, Result : Production> : ProductionParser<Result> {
    val parser: ProductionParser<out TypesToParse>
    val availableOperators: List<TerminalType>
    val produce: (typesToParse: List<TypesToParse>, operators: List<TerminalType>) -> Result

    override fun parse(lexer: BufferedLexer): Result {
        val (typesToParse, operators) = parseOneOrMore(lexer, parser, availableOperators)

        return produce(typesToParse, operators)
    }

    override fun matchesFirstToken(token: Token?) = parser.matchesFirstToken(token)
}

interface OneOrTwoParser<TypeToParse : Production, Result : Production> : ProductionParser<Result> {
    val parser: ProductionParser<out TypeToParse>
    val availableOperators: List<TerminalType>
    val produce: (left: TypeToParse, operator: TerminalType?, right: TypeToParse?) -> Result

    override fun parse(lexer: BufferedLexer): Result {
        val (left, operator, right) = parseOneOrTwo(lexer, parser, availableOperators)

        return produce(left, operator, right)
    }

    override fun matchesFirstToken(token: Token?) = parser.matchesFirstToken(token)
}
