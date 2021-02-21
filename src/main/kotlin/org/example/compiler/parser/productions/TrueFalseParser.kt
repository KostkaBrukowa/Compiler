package org.example.compiler.parser.productions

import org.example.compiler.parser.Scope
import org.example.compiler.lexer.token.TerminalType
import org.example.compiler.lexer.token.Token
import org.example.compiler.parser.BufferedLexer
import org.example.compiler.parser.Production
import org.example.compiler.parser.ProductionParser
import org.example.compiler.parser.productions.subproductions.Assignable
import org.example.compiler.parser.productions.subproductions.TypeObject
import org.example.compiler.parser.utils.advanceAndRequireTerminals
import org.example.compiler.parser.utils.isIn


data class TrueFalse(val isTrue: Boolean) : ConditionElement, Assignable {
    override fun execute(scope: Scope): Production? = this
    override fun getType(scope: Scope): TypeObject = BasicType(TerminalType.BOOLEAN)
}

object TrueFalseParser : ProductionParser<TrueFalse> {
    private val availableKeywords = listOf(TerminalType.TRUE, TerminalType.FALSE)

    override fun parse(lexer: BufferedLexer): TrueFalse {
        val boolean = lexer.advanceAndRequireTerminals(availableKeywords)

        return TrueFalse(boolean.type == TerminalType.TRUE)
    }

    override fun matchesFirstToken(token: Token?): Boolean = token.isIn(availableKeywords)
}


