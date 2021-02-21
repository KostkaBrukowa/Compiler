package org.example.compiler.parser.productions

import org.example.compiler.parser.Scope
import org.example.compiler.parser.Production
import org.example.compiler.parser.productions.subproductions.TypeObject

data class FunParameters(val arguments: List<ArgumentWithType>, val returnType: TypeObject) : Production {
    override fun getType(scope: Scope): TypeObject {
        return FunctionType(arguments.map { it.type }, returnType)
    }
}

