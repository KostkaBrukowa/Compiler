package org.example.compiler.lexer

import org.example.compiler.source.Source
import org.example.compiler.utils.isEndline

class PositionController(private val source: Source) {
    private var row: Int = 1
    private var column: Int = 1

    fun updatePosition(tokenLexeme: String) {
        column += tokenLexeme.length

        seekAfterToken(tokenLexeme)
    }

    fun getCurrentPosition(): Position = Position(row = row, column = column)

    fun getNextTokenFirstChar(): Char? {
        while (true) {
            val currentChar = source.getCurrentChar() ?: break
            val nextChar = source.peekNextChar(1) ?: ""

            if ((currentChar + "$nextChar").isCommentStart()) {
                skipUntilEndline()
                continue
            }

            if (currentChar.isEndline()) handleEndline()

            if (!currentChar.isWhitespace()) break

            column += 1
            source.seekForward(1)
        }

        return source.getCurrentChar()
    }

    private fun handleEndline() {
        column = 0
        row++
    }

    private fun skipUntilEndline() {
        while (source.getCurrentChar() != null && !source.getCurrentChar()!!.isEndline()) {
            source.seekForward(1)
        }
    }

    private fun seekAfterToken(lexeme: String) = source.seekForward(lexeme.length)
}

private fun String.isCommentStart(): Boolean = this == "//"
