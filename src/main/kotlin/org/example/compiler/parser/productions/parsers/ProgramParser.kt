package org.example.compiler.parser.productions.parsers

import org.example.compiler.lexer.token.Token
import org.example.compiler.parser.BufferedLexer
import org.example.compiler.parser.ProductionParser
import org.example.compiler.parser.productions.FunctionDef
import org.example.compiler.parser.productions.Program
import org.example.compiler.parser.productions.utils.parseOneOrMore

object ProgramParser :
    ProductionParser<Program> {

    override fun parse(lexer: BufferedLexer): Program {
        val functionDefs: List<FunctionDef> =
            parseOneOrMore<FunctionDef>(
                lexer,
                FunctionDefParser,
                null
            )

        return Program(functionDefs)
    }

    override fun matchesFirstToken(token: Token?): Boolean =
        FunctionDefParser.matchesFirstToken(token)
}
