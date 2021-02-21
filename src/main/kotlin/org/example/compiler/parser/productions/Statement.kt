package org.example.compiler.parser.productions

import org.example.compiler.parser.Scope
import org.example.compiler.parser.Production
import org.example.compiler.parser.ProductionParser
import org.example.compiler.parser.productions.parsers.*
import org.example.compiler.parser.productions.subproductions.TypeObject
import org.example.compiler.parser.productions.utils.OrParser

interface Statement : Production {
    fun getAllReturnTypes(scope: Scope): List<TypeObject>
}

object StatementParser : OrParser<Statement> {
    override val parsers: List<ProductionParser<out Statement>> = listOf(
        SingleStatementParser,
        StatementBlockParser
    )
}

interface SingleStatement : Statement

object SingleStatementParser : OrParser<SingleStatement> {
    override val parsers: List<ProductionParser<out SingleStatement>> = listOf(
        IfStatementParser,
        ReturnStatementParser,
        InitStatementParser,
        FunctionDefParser
    )
}

data class StatementBlock(val statements: List<Statement>) : Statement {

    override fun execute(scope: Scope): Production? = execute(scope, emptyList())
    override fun checkInvariants(scope: Scope) = checkInvariants(scope, emptyList())
    override fun getAllReturnTypes(scope: Scope): List<TypeObject> = getAllReturnTypes(scope, emptyList())

    fun checkInvariants(scope: Scope, functionArguments: List<ArgumentWithType>) {
        val blockScope = buildCurrentScope(scope, functionArguments)

        statements.forEach {
            it.checkInvariants(blockScope)
        }
    }

    fun getAllReturnTypes(scope: Scope, functionArguments: List<ArgumentWithType>): List<TypeObject> {
        val blockScope = buildCurrentScope(scope, functionArguments)

        return statements.flatMap {
            if (it !is FunctionDef)
                it.getAllReturnTypes(blockScope)
            else emptyList()
        }
    }

    private fun execute(scope: Scope, functionArguments: List<ArgumentWithType>): Production? {
        val blockScope = buildCurrentScope(scope, functionArguments)

        statements.forEach {
            if (it !is Function)
                it.execute(blockScope)?.let { executeResult -> return executeResult }
        }

        return null
    }

    private fun buildCurrentScope(scope: Scope, functionArguments: List<ArgumentWithType>): Scope {
        val blockScope = scope.pushNewBlock()
        blockScope.addAllHoldersToCurrentBlock(functionArguments)

        statements.forEach { if (it is IdReferenceHolder) blockScope.addHolderToCurrentBlock(it) }

        return blockScope
    }
}

