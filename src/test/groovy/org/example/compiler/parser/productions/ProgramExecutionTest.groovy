package org.example.compiler.parser.productions


import org.example.compiler.parser.Scope
import org.example.compiler.parser.BufferedLexer
import org.example.compiler.parser.productions.parsers.ProgramParser
import spock.lang.Specification

import java.util.stream.Collectors

import static org.example.compiler.lexer.LazyLexerTest.buildLazyLexer

class ProgramExecutionTest extends Specification {

    def "expression test"() {
        given:
        def program = """
        fun main(): Number {
            val x: Number = 12;
            val y: Number = 13;
            val z: Number = 12 + 13;
            val a: Number = (z) + (y);
            val b: Number = (z + y) + (y);
            val c: Number = (z + y) + (y + z);
            val d: Number = 12 * 13;
            val e: Number = (z) + (y);
            val f: Number = (z * y) + (y);
            val g: Number = ((z + y) * (y + z));
            val h: Number = a + b + c * g;
            val i: Number = h - 13;
            val j: Number = i / 10;

            return j;
        }
        """

        when:
        def bufferedLexer = new BufferedLexer(buildLazyLexer(program))

        def result = ProgramParser.INSTANCE.parse(bufferedLexer).execute(new Scope())

        then:
        result == new NumberLiteral(10982.0)
    }

    def "condition test"() {
        given:
        def program = """
        fun eleven(): Number { return 11; }
        fun trueValue(): Boolean { return true; }

        fun main(): Number {
            if(1 < 10) {
                if(1 < 12 && 12 > 1) {
                    val x: String = "Jarek";
                    if(x == "Jarek") {
                        if(x != "jarek") {
                            if( 10 < eleven() && (eleven() >= 12 || eleven() >= 11)) {
                                if (!(false != trueValue())) {
                                    return 11;
                                }
                                else if(!false) {
                                    return 42; 
                                }
                            }   
                        }
                    }
                }
            }

            return 0;
        }
        """

        when:
        def bufferedLexer = new BufferedLexer(buildLazyLexer(program))

        def result = ProgramParser.INSTANCE.parse(bufferedLexer).execute(new Scope())

        then:
        result == new NumberLiteral(42)
    }

    def "functions and anonymous functions"() {
        given:
        def program = """
        fun eleven(): Number {
            return 11;
        }

        fun elevenPlusX(x: Number): Number {
            return 11 + x;
        }
        
        fun execute(check: Function<Arg<>, Returns<Number>>): Number {
           return check(); 
        }

        fun main(): Number {
            val i: Number = eleven() + eleven();
            val j: Number = elevenPlusX(5) + elevenPlusX(6) * 3;
            val z: Number = execute((): Number => { return 100; });

            return i + j + z;
        }
        """

        when:
        def bufferedLexer = new BufferedLexer(buildLazyLexer(program))
        def Program = ProgramParser.INSTANCE.parse(bufferedLexer)
        def result = Program.execute(new Scope())

        then:
        result == new NumberLiteral(189)
    }

    def "lists"() {
        given:
        def program = """
        fun starter(): List {
            return [1,2];
        }

        fun powerOf2(x: Number): Number {
            return x * x;
        }
        
        fun main(): List {
            val start: List = starter();
            val extendedStarter: List = start + [3, 4];
            val squared: List = extendedStarter.map(powerOf2);
            val filtered: List = squared.filter((it: Number): Boolean => { if(it < 16) return true; return false; } );
            val doubleMapped: List = filtered
                    .map( (it: Number): Number => { return it * 9; } )
                    .map( (it: Number): Number => { return it / 3; } );

            val multiplyBy10: Function<Arg<>, Returns<Function<Arg<Number>, Returns<Number>>>> =
                (): Function<Arg<Number>, Returns<Number>> => {
                    return (it: Number): Number => { return it * 10; };
                };
                    
            val result: List = doubleMapped.map(multiplyBy10());

            return result;
        }
        """

        when:
        def bufferedLexer = new BufferedLexer(buildLazyLexer(program))
        def Program = ProgramParser.INSTANCE.parse(bufferedLexer)
        def result = Program.execute(new Scope())

        then:
        result == listProduction([30, 120, 270])
    }

    static def listProduction(List<Integer> values) {
        def listValues = values.stream().map({ it -> new NumberLiteral(it) }).collect(Collectors.toList())

        return new ListLiteral(listValues)
    }
}
