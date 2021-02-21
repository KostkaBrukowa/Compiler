package org.example.compiler.parser.productions.parsers

import org.example.compiler.lexer.token.TerminalType
import org.example.compiler.lexer.token.Token
import org.example.compiler.parser.BufferedLexer
import org.example.compiler.parser.ProductionParser
import org.example.compiler.parser.productions.FunParameters
import org.example.compiler.parser.productions.subproductions.TypeObjectParser
import org.example.compiler.parser.productions.utils.parseZeroOrMore
import org.example.compiler.parser.utils.advanceAndRequire
import org.example.compiler.parser.utils.isA

object FunParametersParser :
    ProductionParser<FunParameters> {

    override fun parse(lexer: BufferedLexer): FunParameters {
        lexer.advanceAndRequire(TerminalType.OPEN_PAREN)

        val arguments = parseZeroOrMore(
            lexer,
            ArgumentWithTypeParser,
            TerminalType.COMMA
        )

        lexer.advanceAndRequire(TerminalType.CLOSE_PAREN)
        lexer.advanceAndRequire(TerminalType.COLON, "Type not specified in function parameters.")

        val returnType = TypeObjectParser.parse(lexer)

        return FunParameters(arguments, returnType)
    }

    override fun matchesFirstToken(token: Token?): Boolean = token.isA(
        TerminalType.OPEN_PAREN
    )
}
