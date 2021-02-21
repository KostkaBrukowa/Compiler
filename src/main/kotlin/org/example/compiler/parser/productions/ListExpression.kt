package org.example.compiler.parser.productions

import org.example.compiler.parser.Scope
import org.example.compiler.lexer.token.TerminalType
import org.example.compiler.parser.Production
import org.example.compiler.parser.ProductionParser
import org.example.compiler.parser.productions.parsers.IdReferenceParser
import org.example.compiler.parser.productions.parsers.ListProductionParser
import org.example.compiler.parser.productions.subproductions.TypeObject
import org.example.compiler.parser.productions.utils.OrParser


interface ListAsValue : Production

object ListAsValueParser : OrParser<ListAsValue> {
    override val parsers: List<ProductionParser<out ListAsValue>> = listOf(
        IdReferenceParser,
        ListProductionParser
    )
}


data class ListExpression(val value: ListAsValue, val modifiers: List<ListModifier> = emptyList()) : PrimaryExpression {
    override fun execute(scope: Scope): ListLiteral? {
        val listLiteral = value.execute(scope) as ListLiteral

        return modifiers.fold(listLiteral) { acc, listModifier ->
            listModifier.execute(scope, acc)
        }
    }

    override fun getType(scope: Scope): TypeObject = BasicType(TerminalType.LIST)

    override fun checkInvariants(scope: Scope) {
        if (value.getType(scope) != BasicType(TerminalType.LIST))
            throw TypeError("List modifiers used on type ${value.getType(scope)}. $value")

        modifiers.forEach { it.checkInvariants(scope) }
    }
}

