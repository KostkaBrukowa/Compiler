package org.example.compiler.parser

import org.example.compiler.lexer.token.Token

interface ProductionParser<ProductionType : Production> {

    fun parse(lexer: BufferedLexer): ProductionType
    fun matchesFirstToken(token: Token?): Boolean

}
