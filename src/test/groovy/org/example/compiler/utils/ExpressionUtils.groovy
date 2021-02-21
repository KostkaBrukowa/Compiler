package org.example.compiler.utils

import org.example.compiler.parser.productions.*

import java.util.stream.Collectors

class ExpressionUtils {

    static def buildNumberExpression(Float number) {

    }

//    static def single(Number n) {
//        return new Expression([new MultiplicativeExpression([new NumberLiteral(n)], [])], [])
//    }
//
//    static def single(String n) {
//        return new Expression([new MultiplicativeExpression([new StringLiteral(n)], [])], [])
//    }

    static def expr(PrimaryExpression expr) {
        return new Expression([new MultiplicativeExpression([expr], [])], [])
    }

    static def cond(Boolean negated, ConditionElement cond) {
        return new Condition([
                new AndCondition([
                        new EqualityCondition(
                                new RelationalCondition(
                                        new PrimaryCondition(negated, cond),
                                        null,
                                        null
                                ),
                                null,
                                null
                        ),
                ]),
        ])
    }

    static def listLiteral(List<PrimaryExpression> exprs) {
        def expressions = exprs.stream().map { it -> expr(it) }.collect(Collectors.toList())

        return new ListExpression(new ListProduction(expressions), [])
    }

}
