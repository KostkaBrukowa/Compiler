package org.example.compiler.parser.productions

import org.example.compiler.parser.Scope
import org.example.compiler.parser.Production

class Program(val functionDefs: List<FunctionDef>) : Production {
    override fun execute(scope: Scope): Production? {
        scope.addAllHoldersToCurrentBlock(functionDefs)

        if (scope.getLatestIdHolder("main") == null) throw MainNotDefinedException()

        functionDefs.forEach { it.checkInvariants(scope) }

        return scope.getLatestIdHolder("main")?.execute(scope.pushNewBlock())
    }
}


class MainNotDefinedException : Exception("Main function was not defined")
