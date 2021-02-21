package org.example.compiler.parser.productions

import org.example.compiler.parser.IdentifierRedefinitionException
import org.example.compiler.parser.Scope
import org.example.compiler.parser.BufferedLexer
import org.example.compiler.parser.productions.parsers.ProgramParser
import spock.lang.Specification
import spock.lang.Unroll

import static org.example.compiler.lexer.LazyLexerTest.buildLazyLexer

class ProgramTest extends Specification {


    def "should throw when type of return do not match type from function declaration"() {
        given:
        def program = """
        fun main(): Number {
            val x: Number = 12;

            if(x > 2) {
                val y: String = "jarek";

                return y;
            }
            else {

            }

            return "jarek";
        }
        """

        when:
        def bufferedLexer = new BufferedLexer(buildLazyLexer(program))
        def Program = ProgramParser.INSTANCE.parse(bufferedLexer)
        Program.execute(new Scope())

        then:
        def error = thrown(TypeError)
        error.message.contains("One of return types of function was incorrect")
    }

    def "should not throw after correct basic operations"() {
        given:
        def program = """
        fun main(): Number {
            val x: Number = 12;

            if(x > 2) {
                val x: Number = 11;

                return x;
            }
            else {

            }

            return 1;
        }
        """

        when:
        def bufferedLexer = new BufferedLexer(buildLazyLexer(program))
        ProgramParser.INSTANCE.parse(bufferedLexer)

        then:
        noExceptionThrown()
    }

    def "should throw when variable name is redefined"() {
        given:
        def program = """
        fun main(): Number {
            val redefinedVariable: Number = 12;

            if(redefinedVariable > 2) {
                val redefinedVariable: String = "jarek";

                return redefinedVariable;
            }
            else {

            }

            return 22;
        }
        """

        when:
        def bufferedLexer = new BufferedLexer(buildLazyLexer(program))
        def Program = ProgramParser.INSTANCE.parse(bufferedLexer)
        Program.execute(new Scope())

        then:
        def error = thrown(IdentifierRedefinitionException)
        error.message.contains("redefinedVariable")
    }

    def "should throw when just one of the return types is incorrect"() {
        given:
        def program = """
        fun main(): Number {
            val x: Number = 12;

            if(x > 2) {
                val y: Number = 11;
                if(y > 3) return y;
                else {
                    if(y > 4) {
                        return "jarek";
                    }
                }

                return y;
            }
            else {

            }

            return 22;
        }
        """

        when:
        def bufferedLexer = new BufferedLexer(buildLazyLexer(program))
        def Program = ProgramParser.INSTANCE.parse(bufferedLexer)
        Program.execute(new Scope())

        then:
        def error = thrown(TypeError)
        error.message.contains("One of return types of function was incorrect")
    }

    def "should not throw when return with wrong return type is declared in inline function"() {
        given:
        def program = """
        fun main(): Number {
            val x: Number = 12;
            
            fun funInFun(y: String): String {
              return y;
            }

            return 22;
        }
        """

        when:
        def bufferedLexer = new BufferedLexer(buildLazyLexer(program))
        def Program = ProgramParser.INSTANCE.parse(bufferedLexer)
        Program.execute(new Scope())

        then:
        noExceptionThrown()
    }

    def "should throw when wrong return type is declared in inline function"() {
        given:
        def program = """
        fun main(): Number {
            val x: Number = 12;
            
            fun funInFun(): String {
              return 11;
            }

            return 22;
        }
        """

        when:
        def bufferedLexer = new BufferedLexer(buildLazyLexer(program))
        def Program = ProgramParser.INSTANCE.parse(bufferedLexer)
        Program.execute(new Scope())

        then:
        def error = thrown(TypeError)
        error.message.contains("STRING")
    }

    def "should not throw when function variables are correctly declared"() {
        given:
        def program = """
        fun main(): Number {
            val x: Number = 12;
            
            val funInVal: Function<Arg<Boolean>, Returns<Boolean>> = (y: Boolean): Boolean => { return y; };

            return 22;
        }
        """

        when:
        def bufferedLexer = new BufferedLexer(buildLazyLexer(program))
        def Program = ProgramParser.INSTANCE.parse(bufferedLexer)
        Program.execute(new Scope())

        then:
        noExceptionThrown()
    }

    def "should throw when wrong return value is used in anonymous function"() {
        given:
        def program = """
        fun main(): Number {
            val funInVal: Function<Arg<Boolean>, Returns<Boolean>> = (y: Boolean): Boolean => { return 11; };

            return 22;
        }
        """

        when:
        def bufferedLexer = new BufferedLexer(buildLazyLexer(program))
        def Program = ProgramParser.INSTANCE.parse(bufferedLexer)
        Program.execute(new Scope())

        then:
        def error = thrown(TypeError)
        error.message.contains("BOOLEAN")
        error.message.contains("NUMBER")
    }

    def "should throw when wrong return type does not match variable definition type is used in anonymous function"() {
        given:
        def program = """
        fun main(): Number {
            val funInVal: Function<Arg<Boolean, String>, Returns<Boolean>> = (y: Boolean, z: Boolean): Boolean => { return true; };

            return 22;
        }
        """

        when:
        def bufferedLexer = new BufferedLexer(buildLazyLexer(program))
        def Program = ProgramParser.INSTANCE.parse(bufferedLexer)
        Program.execute(new Scope())

        then:
        def error = thrown(TypeError)
        error.message.contains("BOOLEAN")
    }

    def "should throw when different types are compared in condition"() {
        given:
        def program = """
        fun main(): Number {
            val x: Boolean = false;
            if(x > 11) {
                return 42;
            }

            return 22;
        }
        """

        when:
        def bufferedLexer = new BufferedLexer(buildLazyLexer(program))
        def Program = ProgramParser.INSTANCE.parse(bufferedLexer)
        Program.execute(new Scope())

        then:
        thrown(ComparisonOfNonNumbersError)
    }

    def "should throw when one of different types is compared in condition"() {
        given:
        def program = """
        fun main(): Number {
            val x: Boolean = true;
            val y: Number = 13;
            if((x > 11) && (x < 13)) {
                return 42;
            }

            return 22;
        }
        """

        when:
        def bufferedLexer = new BufferedLexer(buildLazyLexer(program))
        def Program = ProgramParser.INSTANCE.parse(bufferedLexer)
        Program.execute(new Scope())

        then:
        thrown(ComparisonOfNonNumbersError)
    }

    def "should throw when one of different types is compared in condition1"() {
        given:
        def program = """
        fun main(): Number {
            val x: Boolean = true;
            val y: Number = 13;

            if(((x) > (11)) && x > y) {
                return 45;
            }

            return 22;
        }
        """

        when:
        def bufferedLexer = new BufferedLexer(buildLazyLexer(program))
        def Program = ProgramParser.INSTANCE.parse(bufferedLexer)
        Program.execute(new Scope())

        then:
        thrown(ComparisonOfNonNumbersError)
    }

    def "should throw when booleans are compared"() {
        given:
        def program = """
        fun main(): Number {
            val x: Boolean = true;
            val y: Number = 13;

            if(true > true) {
                return 45;
            }

            return 22;
        }
        """

        when:
        def bufferedLexer = new BufferedLexer(buildLazyLexer(program))
        def Program = ProgramParser.INSTANCE.parse(bufferedLexer)
        Program.execute(new Scope())

        then:
        thrown(ComparisonOfNonNumbersError)
    }

    def "should throw when booleans are compared1"() {
        given:
        def program = """
        fun main(): Number {
            val x: Boolean = true;
            val y: Number = 13;

            if((y > 1) > true) {
                return 45;
            }

            return 22;
        }
        """

        when:
        def bufferedLexer = new BufferedLexer(buildLazyLexer(program))
        def Program = ProgramParser.INSTANCE.parse(bufferedLexer)
        Program.execute(new Scope())

        then:
        thrown(ComparisonOfNonNumbersError)
    }

    def "should not throw when all variables in condition are correct type"() {
        given:
        def program = """
        fun main(): Number {
            val x: Number = 12;
            val y: Number = 13;
            if((x > 11) && (x < 13)) {
                return 42;
            }

            if(((x) > (11))) {
                return 43;
            }

            if(((x) > (11)) && x > y) {
                return 45;
            }
            if(x == 12 && x != 12 && ((x > 12) != (x < 13))) {
                return 45;
            }

            return 22;
        }
        """

        when:
        def bufferedLexer = new BufferedLexer(buildLazyLexer(program))
        def Program = ProgramParser.INSTANCE.parse(bufferedLexer)
        Program.execute(new Scope())

        then:
        noExceptionThrown()
    }

    def "should throw when different types are used in single expression"() {
        given:
        def program = """
        fun main(): Number {
            val z: Number =  12 + 13 + "jarek";

            return 22;
        }
        """

        when:
        def bufferedLexer = new BufferedLexer(buildLazyLexer(program))
        def Program = ProgramParser.INSTANCE.parse(bufferedLexer)
        Program.execute(new Scope())

        then:
        thrown(ExpressionTypeMismatch)
    }

    def "should throw when different types are used in single expression1"() {
        given:
        def program = """
        fun main(): Number {
            val x: String = "ajrek";
            val z: Number =  ( ( (1) + 12 ) + ((13) + x));

            return 22;
        }
        """

        when:
        def bufferedLexer = new BufferedLexer(buildLazyLexer(program))
        def Program = ProgramParser.INSTANCE.parse(bufferedLexer)
        Program.execute(new Scope())

        then:
        thrown(ExpressionTypeMismatch)
    }

    def "should throw when multiplying strings"() {
        given:
        def program = """
        fun main(): Number {
            val z: String = "Jarek" * "darek";

            return 22;
        }
        """

        when:
        def bufferedLexer = new BufferedLexer(buildLazyLexer(program))
        def Program = ProgramParser.INSTANCE.parse(bufferedLexer)
        Program.execute(new Scope())

        then:
        thrown(MultiplyError)
    }

    def "should throw when types are not correct in expression"() {
        given:
        def program = """
        fun main(): Number {
            val x: Number = 12;
            val xx: String = ("Jarek");
            val xxx: String = (x + 11) * (xx + x);
            

            return 22;
        }
        """

        when:
        def bufferedLexer = new BufferedLexer(buildLazyLexer(program))
        def Program = ProgramParser.INSTANCE.parse(bufferedLexer)
        Program.execute(new Scope())

        then:
        def error = thrown(ExpressionTypeMismatch)
        error.message.contains("BasicType(type=STRING) expected got BasicType(type=NUMBER)")
    }

    def "should not throw when all types in expression are correct"() {
        given:
        def program = """
        fun main(): Number {
            val x: Number = 12;
            val y: Number = 13;
            val xx: String = ("Jarek");
            val xa: String = "Jarek" + "darek";
            val z: Number = 12 + 13;
            val a: Number = (z) + (y);
            val b: Number = (z + y) + (y);
            val c: Number = (z + y) + (y + z);
            val d: Number = 12 * 13;
            val e: Number = (z) + (y);
            val f: Number = (z * y) + (y);
            val g: Number = ((z + y) * (y + z));
            val h: Number = a + b + c * g;
            

            return 22;
        }
        """

        when:
        def bufferedLexer = new BufferedLexer(buildLazyLexer(program))
        def Program = ProgramParser.INSTANCE.parse(bufferedLexer)
        Program.execute(new Scope())

        then:
        noExceptionThrown()
    }

    @Unroll
    def "should throw when list types are not numbers #listDeclaration"() {
        given:
        def program = """
        fun main(): Number {
            val boolean: Boolean = true;
            val string: String = true;
            val function: Function<Arg<>, Returns<Boolean>> = (): Number => { return 11; };
            val list: List = ${listDeclaration};            

            return 22;
        }
        """

        when:
        def bufferedLexer = new BufferedLexer(buildLazyLexer(program))
        def Program = ProgramParser.INSTANCE.parse(bufferedLexer)
        Program.execute(new Scope())

        then:
        thrown(TypeError)

        where:
        listDeclaration << ['[1,2, string]', '[1,2, boolean]', '[1,2, function]']
    }

    @Unroll
    def "should throw when wrong callback: #callback is passed to list modifier #modifier"() {
        given:
        def program = """
        fun main(): Number {
            val callback: ${callback}
        
            val list: List = [1,2].${modifier}(callback);            

            return 22;
        }
        """

        when:
        def bufferedLexer = new BufferedLexer(buildLazyLexer(program))
        def Program = ProgramParser.INSTANCE.parse(bufferedLexer)
        Program.execute(new Scope())

        then:
        thrown(InvalidListModifierCallback)

        where:
        modifier | callback
        'map'    | 'Function<Arg<>, Returns<Boolean>> = (): Boolean => { return true; };'
        'map'    | 'Function<Arg<Number>, Returns<Boolean>> = (it: Number): Boolean => { return true; };'
        'map'    | 'Function<Arg<>, Returns<Number>> = (): Number => { return 11; };'
        'filter' | 'Function<Arg<>, Returns<Number>> = (): Number => { return 11; };'
        'filter' | 'Function<Arg<Number>, Returns<Number>> = (it: Number): Number => { return 11; };'
        'filter' | 'Function<Arg<>, Returns<Boolean>> = (): Boolean => { return true; };'
    }

    def "should not throw when correct callback: #callback is passed to list modifier #modifier"() {
        given:
        def program = """
        fun main(): Number {
            val callback: ${callback}
        
            val list: List = [1,2].${modifier}(callback);            

            return 22;
        }
        """

        when:
        def bufferedLexer = new BufferedLexer(buildLazyLexer(program))
        def Program = ProgramParser.INSTANCE.parse(bufferedLexer)
        Program.execute(new Scope())

        then:
        noExceptionThrown()

        where:
        modifier | callback
        'map'    | 'Function<Arg<Number>, Returns<Number>> = (it: Number): Number => { return 11; };'
        'filter' | 'Function<Arg<Number>, Returns<Boolean>> = (it: Number): Boolean => { return true; };'
    }
}
