package org.example.compiler

import org.example.compiler.parser.Scope
import org.example.compiler.lexer.LazyLexer
import org.example.compiler.lexer.errorhandlers.LexerErrorHandlingDecorator
import org.example.compiler.parser.BufferedLexer
import org.example.compiler.parser.productions.parsers.ProgramParser
import org.example.compiler.source.Source
import java.io.File
import java.io.IOException

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("Please give a filename as an argument.")
        return
    }

    try {
        val source = Source(File(args[0]).inputStream().bufferedReader())
        val lexer = LexerErrorHandlingDecorator(LazyLexer(source))
        val program = ProgramParser.parse(BufferedLexer(lexer))
        print(program.execute(Scope()))
    } catch (e: IOException) {
        println("There was a problem with reading a file")
    }
}
