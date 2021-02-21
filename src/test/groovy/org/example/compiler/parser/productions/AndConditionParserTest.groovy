package org.example.compiler.parser.productions

import org.example.compiler.parser.BufferedLexer
import org.example.compiler.parser.Lexer
import org.example.compiler.parser.productions.parsers.AndConditionParser
import org.example.compiler.source.Source
import spock.lang.Specification
import spock.lang.Unroll

import static org.example.compiler.lexer.LazyLexerTest.buildLazyLexer
import static org.example.compiler.parser.productions.EqualityConditionParserTest.relationalCond
import static org.example.compiler.utils.ExpressionUtils.expr

class AndConditionParserTest extends Specification {
    Lexer lazyLexer

    @Unroll
    def "should return correct AndCondition from string '#inputString'"() {
        given:
        lazyLexer = buildLazyLexer(new Source(new StringReader(inputString)))

        and:
        def bufferedLexer = new BufferedLexer(lazyLexer)

        when:
        def AndCondition = AndConditionParser.INSTANCE.parse(bufferedLexer)

        then:

        with(AndCondition) {
            equalityConditions == expectedEqualityConditions
        }


        where:
        expectedEqualityConditions                                                                | inputString
        [equalityCond("a"), equalityCond("b")]                                                    | 'a && b'
        [equalityCond("a"), equalityCond("b", new FunCall([]))]                                   | 'a && b()'
        [equalityCond("a"), equalityCond("a", new IndexingReference(expr(new NumberLiteral(1))))] | 'a && a[1]'
    }

    def equalityCond(String id, IdReferenceArgument argument = null) {
        return new EqualityCondition(relationalCond(id, argument), null, null)
    }
}

