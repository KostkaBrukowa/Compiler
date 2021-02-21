package org.example.compiler.parser.productions

import org.example.compiler.parser.ProductionParser
import org.example.compiler.parser.productions.subproductions.Assignable
import org.example.compiler.parser.productions.utils.OrParser

interface Literal : PrimaryExpression, Assignable, ConditionElement

object LiteralParser : OrParser<Literal> {
    override val parsers: List<ProductionParser<out Literal>> = listOf(
        NumberLiteralParser,
        StringLiteralParser
    )
}
