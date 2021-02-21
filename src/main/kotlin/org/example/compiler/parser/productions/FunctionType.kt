package org.example.compiler.parser.productions

import org.example.compiler.parser.Scope
import org.example.compiler.parser.productions.subproductions.TypeObject

data class FunctionType(val argumentsTypes: List<TypeObject>, val returnType: TypeObject) : TypeObject {
    override fun getType(scope: Scope): TypeObject = this
}

