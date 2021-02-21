package org.example.compiler.parser.productions

import org.example.compiler.parser.Scope
import org.example.compiler.lexer.token.TerminalType
import org.example.compiler.parser.productions.subproductions.TypeObject


data class BasicType(val type: TerminalType) : TypeObject {
    override fun getType(scope: Scope): TypeObject = this
}

