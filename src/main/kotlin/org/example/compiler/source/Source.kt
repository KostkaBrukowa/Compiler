package org.example.compiler.source

import java.io.Reader


class Source(private val reader: Reader) {
    companion object {
        const val EOF = 65535
    }

    private var currentBuffer = ""

    private fun Char.nullIfEOF(): Char? = if (this.toInt() == EOF) null else this

    private fun readNextChar() = reader.read().toChar().toString()

    private fun extendBufferIfNeeded(offset: Int) {
        val currentBufferMaxIndex = currentBuffer.length - 1

        if (offset > currentBufferMaxIndex) {
            val charArray = CharArray(offset - currentBufferMaxIndex) { EOF.toChar() }

            reader.read(charArray, 0, offset - currentBufferMaxIndex)
            currentBuffer += charArray.joinToString()
        }
    }

    fun getCurrentChar(): Char? {
        if (currentBuffer.isEmpty()) {
            currentBuffer = readNextChar()
        }

        return currentBuffer.first().nullIfEOF()
    }

    fun peekNextChar(offset: Int): Char? {
        extendBufferIfNeeded(offset)

        return currentBuffer[offset].nullIfEOF()
    }

    fun seekForward(offset: Int) {
        if (offset < currentBuffer.length) {
            currentBuffer = currentBuffer.substring(offset)
            return
        }

        currentBuffer = readNextChar()
    }
}
