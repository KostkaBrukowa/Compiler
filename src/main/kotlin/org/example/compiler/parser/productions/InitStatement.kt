package org.example.compiler.parser.productions


import org.example.compiler.parser.Scope
import org.example.compiler.parser.Production
import org.example.compiler.parser.productions.subproductions.Assignable
import org.example.compiler.parser.productions.subproductions.TypeObject

data class InitStatement(
    override val id: String,
    val argumentWithType: ArgumentWithType,
    val assignable: Assignable
) : SingleStatement, IdReferenceHolder {
    override fun execute(scope: Scope): Production? {
        val assignableValue = if (assignable is AnonymousFunction) assignable else assignable.execute(scope)
            ?: throw NullFromNotNullableProduction(assignable)

        scope.modifyIdValue(id, assignableValue)

        return null
    }

    override fun checkInvariants(scope: Scope) {
        assignable.checkInvariants(scope)

        if (argumentWithType.type != assignable.getType(scope))
            throw InitStatementTypeMismatch(argumentWithType, assignable, scope)
    }

    override fun getAllReturnTypes(scope: Scope): List<TypeObject> = emptyList()
    override fun getType(scope: Scope): TypeObject = argumentWithType.getType(scope)
}


class InitStatementTypeMismatch(
    argumentWithType: ArgumentWithType,
    assignable: Assignable,
    scope: Scope
) : TypeError(
    "Assignable that was assigned to ${argumentWithType.id} was incorrect type. Expected ${argumentWithType.type} got ${assignable.getType(
        scope
    )}"
)

class NullFromNotNullableProduction(production: Production?) : Throwable("$production")
