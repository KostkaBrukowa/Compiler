package org.example.compiler.parser.productions.parsers

import org.example.compiler.lexer.token.TerminalType
import org.example.compiler.lexer.token.Token
import org.example.compiler.parser.BufferedLexer
import org.example.compiler.parser.ProductionParser
import org.example.compiler.parser.productions.FunctionType
import org.example.compiler.parser.productions.subproductions.TypeObjectParser
import org.example.compiler.parser.productions.utils.parseZeroOrMore
import org.example.compiler.parser.utils.advanceAndRequire
import org.example.compiler.parser.utils.isA

object FunctionTypeParser : ProductionParser<FunctionType> {

    override fun parse(lexer: BufferedLexer): FunctionType {
        lexer.advanceAndRequire(TerminalType.FUNCTION)
        lexer.advanceAndRequire(TerminalType.LESS_THAN)
        lexer.advanceAndRequire(TerminalType.ARG)
        lexer.advanceAndRequire(TerminalType.LESS_THAN)

        val argumentsTypes = parseZeroOrMore(
            lexer,
            TypeObjectParser,
            TerminalType.COMMA
        )

        lexer.advanceAndRequire(TerminalType.GREATER_THAN)
        lexer.advanceAndRequire(TerminalType.COMMA)
        lexer.advanceAndRequire(TerminalType.RETURNS)
        lexer.advanceAndRequire(TerminalType.LESS_THAN)

        val returnType = TypeObjectParser.parse(lexer)

        lexer.advanceAndRequire(TerminalType.GREATER_THAN)
        lexer.advanceAndRequire(TerminalType.GREATER_THAN)

        return FunctionType(argumentsTypes, returnType)
    }

    override fun matchesFirstToken(token: Token?): Boolean = token.isA(
        TerminalType.FUNCTION
    )
}
