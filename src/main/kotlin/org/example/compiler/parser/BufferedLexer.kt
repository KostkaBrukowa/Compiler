package org.example.compiler.parser

import org.example.compiler.lexer.TokenMeta

open class BufferedLexer(private val lexer: Lexer) {

    private var currentBuffer: MutableList<TokenMeta?> = mutableListOf(null)

    private fun readNextTokenMeta() = lexer.getNextTokenMeta()

    private fun extendBufferIfNeeded(offset: Int) {
        val currentBufferMaxIndex = currentBuffer.size - 1

        if (offset > currentBufferMaxIndex) {
            for (i in 0 until offset - currentBufferMaxIndex) {
                currentBuffer.add(readNextTokenMeta())
            }
        }
    }

    fun getCurrentTokenMeta(): TokenMeta? {
        return currentBuffer.first()
    }

    fun peekNextTokenMeta(offset: Int = 1): TokenMeta? {
        extendBufferIfNeeded(offset)

        return currentBuffer[offset]
    }

    fun getNextTokenMeta(offset: Int = 1): TokenMeta? {
        currentBuffer = if (offset < currentBuffer.size)
            currentBuffer.subList(offset, currentBuffer.size) else mutableListOf(readNextTokenMeta())

        return getCurrentTokenMeta()
    }
}
