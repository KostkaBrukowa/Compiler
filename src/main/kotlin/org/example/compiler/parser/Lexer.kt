package org.example.compiler.parser

import org.example.compiler.lexer.TokenMeta

interface Lexer {
    fun getNextTokenMeta(): TokenMeta?
}
