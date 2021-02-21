package org.example.compiler.parser.productions

import org.example.compiler.parser.BufferedLexer
import org.example.compiler.parser.Lexer
import org.example.compiler.parser.productions.parsers.ConditionParser
import spock.lang.Specification
import spock.lang.Unroll

import static org.example.compiler.lexer.LazyLexerTest.buildLazyLexer
import static org.example.compiler.parser.productions.EqualityConditionParserTest.relationalCond
import static org.example.compiler.utils.ExpressionUtils.expr

class ConditionParserTest extends Specification {
    Lexer lazyLexer

    @Unroll
    def "should return correct Condition from string '#inputString'"() {
        given:
        lazyLexer = buildLazyLexer(inputString)

        and:
        def bufferedLexer = new BufferedLexer(lazyLexer)

        when:
        def Condition = ConditionParser.INSTANCE.parse(bufferedLexer)

        then:
        with(Condition) {
            andConditions == expectedAndConditions
        }

        where:
        expectedAndConditions                                                           | inputString
        [andCond("a"), andCond("b")]                                                    | 'a || b'
        [andCond("a"), andCond("b", new FunCall([]))]                                   | 'a || b()'
        [andCond("a"), andCond("a", new IndexingReference(expr(new NumberLiteral(1))))] | 'a || a[1]'
    }

    static def andCond(String id, IdReferenceArgument argument = null) {
        return new AndCondition([new EqualityCondition(relationalCond(id, argument), null, null)])
    }
}


