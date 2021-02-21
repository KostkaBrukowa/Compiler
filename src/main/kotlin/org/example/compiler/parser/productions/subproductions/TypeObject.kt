package org.example.compiler.parser.productions.subproductions

import org.example.compiler.parser.Production
import org.example.compiler.parser.ProductionParser
import org.example.compiler.parser.productions.parsers.BasicTypeParser
import org.example.compiler.parser.productions.parsers.FunctionTypeParser
import org.example.compiler.parser.productions.utils.OrParser

interface TypeObject : Production

object TypeObjectParser : OrParser<TypeObject> {
    override val parsers: List<ProductionParser<out TypeObject>> = listOf(
        BasicTypeParser,
        FunctionTypeParser
    )
}
