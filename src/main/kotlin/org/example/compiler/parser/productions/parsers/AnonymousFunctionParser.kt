package org.example.compiler.parser.productions.parsers

import org.example.compiler.lexer.token.TerminalType
import org.example.compiler.lexer.token.Token
import org.example.compiler.parser.BufferedLexer
import org.example.compiler.parser.ProductionParser
import org.example.compiler.parser.productions.AnonymousFunction
import org.example.compiler.parser.utils.advanceAndRequire

object AnonymousFunctionParser :
    ProductionParser<AnonymousFunction> {

    override fun parse(lexer: BufferedLexer): AnonymousFunction {
        val parameters = FunParametersParser.parse(lexer)

        lexer.advanceAndRequire(TerminalType.ARROW)

        val statementBlock = StatementBlockParser.parse(lexer)

        return AnonymousFunction(parameters, statementBlock)
    }

    override fun matchesFirstToken(token: Token?): Boolean =
        FunParametersParser.matchesFirstToken(token)
}
