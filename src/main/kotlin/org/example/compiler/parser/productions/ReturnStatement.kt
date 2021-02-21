package org.example.compiler.parser.productions

import org.example.compiler.parser.Scope
import org.example.compiler.parser.Production
import org.example.compiler.parser.productions.subproductions.Assignable
import org.example.compiler.parser.productions.subproductions.TypeObject

data class ReturnStatement(val assignable: Assignable) : SingleStatement {
    override fun execute(scope: Scope): Production? =
        if (assignable is AnonymousFunction) assignable else assignable.execute(scope)
            ?: throw NullFromNotNullableProduction(assignable)

    override fun getAllReturnTypes(scope: Scope): List<TypeObject> = listOf(assignable.getType(scope))
}

