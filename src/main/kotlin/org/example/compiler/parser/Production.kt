package org.example.compiler.parser

import org.example.compiler.parser.productions.subproductions.TypeObject

interface Production {
    fun execute(scope: Scope): Production? {
        return null
    }

    fun checkInvariants(scope: Scope) {}
    fun getType(scope: Scope): TypeObject {
        throw RuntimeException("$this Not type specified")
    }
}
