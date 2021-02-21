package org.example.compiler.parser.productions.parsers

import org.example.compiler.lexer.token.TerminalType
import org.example.compiler.lexer.token.Token
import org.example.compiler.parser.BufferedLexer
import org.example.compiler.parser.ProductionParser
import org.example.compiler.parser.productions.FunctionDef
import org.example.compiler.parser.utils.advanceAndRequire
import org.example.compiler.parser.utils.isA

object FunctionDefParser : ProductionParser<FunctionDef> {

    override fun parse(lexer: BufferedLexer): FunctionDef {
        lexer.advanceAndRequire(TerminalType.FUN)

        val idReference =
            IdReferenceParser.parseOnlyId(lexer)

        val parameters = FunParametersParser.parse(lexer)

        val statement = StatementBlockParser.parse(lexer)

        return FunctionDef(idReference.id, parameters, statement)
    }

    override fun matchesFirstToken(token: Token?): Boolean = token.isA(
        TerminalType.FUN
    )
}
