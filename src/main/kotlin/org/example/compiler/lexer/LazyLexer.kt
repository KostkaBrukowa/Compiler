package org.example.compiler.lexer

import org.example.compiler.lexer.recognisers.*
import org.example.compiler.lexer.token.Token
import org.example.compiler.lexer.token.UnrecognisedToken
import org.example.compiler.parser.Lexer
import org.example.compiler.source.Source

class LazyLexer(
    private val source: Source,
    singleWordRecogniser: SingleWordRecogniser = SingleWordRecogniser(),
    numberRecogniser: NumberRecogniser = NumberRecogniser(),
    stringRecogniser: StringRecogniser = StringRecogniser(),
    terminalRecogniser: TerminalRecogniser = TerminalRecogniser()
) : Lexer {
    private val recognisers: List<TokenRecogniser> = listOf(
        numberRecogniser,
        terminalRecogniser,
        stringRecogniser,
        singleWordRecogniser
    )
    private val positionController = PositionController(source)

    override fun getNextTokenMeta(): TokenMeta? {
        val currentChar = positionController.getNextTokenFirstChar() ?: return null
        val previousPosition = positionController.getCurrentPosition()

        recognisers.forEach { recogniser ->
            recogniser.recognise(source)?.let { tokenLexeme ->
                val token = recogniser.convertToToken(tokenLexeme)

                positionController.updatePosition(tokenLexeme)

                return TokenMeta(token, previousPosition)
            }
        }

        source.seekForward(1) // we only have unrecognised tokens that consists of 1 letter
        return TokenMeta(UnrecognisedToken(currentChar.toString()), previousPosition)
    }
}


data class Position(val row: Int, val column: Int)

data class TokenMeta(val token: Token, val position: Position)

