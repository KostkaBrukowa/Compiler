package org.example.compiler.parser.productions

import org.example.compiler.parser.Scope
import org.example.compiler.parser.productions.subproductions.TypeObject

data class ArgumentWithType(override val id: String, val type: TypeObject) : IdReferenceHolder {
    override fun getType(scope: Scope): TypeObject = type
}

