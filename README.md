# Kompilator dla języka funkcyjnego z leniwą ewaluacją
### Autor: Jarosław Glegoła
#### Opis projektu
Ideą projektu jest stworzenie leniwego języka funkcyjnego, wyposażonego w podstawowe funkcje modyfikujące (filter, map), możliwość definiowania własnych funkcji, stałych oraz wbudowany typ tablicy (listy) liczb, instrukcji warunkowych.

## Przyjęte zostały następujące założenia:
- Jedynym typem wbudowanym (oprócz podstawowych stringów, liczb, booleanów) jest typ tablicy, który wewnątrz siebie przechowuje tylko i wyłącznie liczby.
- Język nie ma mutowanych zmiennych, można tworzyć tylko niemutowalne stałe
- Funkcje nazwane (wyrażenia zaczynające się od `fun` mogą być definiowane tylko i wyłącznie w głównym zasięgu programu i nie mogą być nadpisywane.
- Funkcje anonimowe i nazwane (zaczynające się od `fun`) mogą być przypisywane do stałych i przekazywane do funkcji
- Każdy program musi zawierać funkcję `main`, będącą punktem wejściowym dla jednostki wykonującej program.

## Wymagania funkcjonalne 
- Odczytywanie, parsowanie i analiza kodu źródłowego zapisanego w plikach tekstowych oraz standardowego wejścia
- Kontrola poprawności wprowadzonych danych oraz poprawne zgłaszanie błędów wykrytych podczas kolejnych etapów analizy
- Poprawne wykonywanie wszystkich poprawnie zapisanych instrukcji w plikach 
- Przeprowadzanie operacji na listach
## Wymagania niefunkcjonalne
- Uruchomienie aplikacji z niepoprawnymi parametrami powinno poinformować użytkownika w przejrzysty sposób o błędach w kodzie źródłowym
## Środowisko i obsługa programu
Środowisko uruchomieniowe i wybrane technologie
Projekt będzie jako aplikacja czysto konsolowa. Projekt będzie pisany w języku Kotlin, zaś do testowania jednostkowego kodu będzie użyty język Groovy wraz z frameworkiem Spock (http://spockframework.org/spock/docs/1.3/all_in_one.html)

## Obsługa programu
Program będzie prostą aplikacja konsolową, uruchamianą poprzez wywołanie wraz z parametrem uruchomieniowym reprezentującym ścieżkę do pliku z kodem do kompilacji.
Wynik poszczególnych etapów analizy pliku oraz samego wyniku interpretacji końcowej i wykonania będzie wyświetlany na standardowym wyjściu.

Przykład:
```
./compiler ./text.ljf
```
## Biblioteka standardowa
Wstępnie przewidywana biblioteka funkcji wbudowanych:
- Wbudowane funkcje dostępne dla tablic
  - map(transformFunction) - Zwraca nowa tablicę, która jest wynikiem wykonania transformFunction na wszystkich elementach.
  - filter(predicateFunction) Zwraca nowa tablicę, która posiada tylko te elementy, które dla predicateFunction zwraca true
  - operator `+` zdefiniowany dla list np. `[1,2] + [2,3] -> [1,2,2,3]`.

## Przykłady:
Inicjowanie stałych i funkcji
```
val foo: Number = 1;
val bar: Number = 2;
val baz: Number = foo + bar * foo;
val list: List = [1, 2, foo];
fun someFunction(arg: List): Boolean {
	return true;
}
```
Operacje na listach
```
val list: List = [1,2,3];
val transformedList: List = list.map((x: Number): Number => { return x + 1 });
val filteredList: List = transformedList.filter((x: Number): Boolean => { return if(x == 1) true else return false; });
```

Przykład programu
```
fun eleven(): Number {
    return 11;
}

fun elevenPlusX(x: Number): Number {
    return 11 + x;
}

fun execute(check: Function<Arg<>, Returns<Number>>): Number {
   return check();
}

fun starter(): List {
    return [1,2];
}

fun powerOf2(x: Number): Number {
    return x * x;
}

fun trueValue(): Boolean { return true; }

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


    val twentyTwo: Number = eleven() + eleven();
    val thirtySix: Number = elevenPlusX(5) + elevenPlusX(6) * 3;
    val hundred: Number = execute((): Number => { return 100; });


    if(1 < 10) {
        if(1 < 12 && 12 > 1) {
            val some: String = "Jarek";
            if(some == "Jarek") {
                if(some != "jarek") {
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
```

## Gramatyka
```
program = { functionDef } .
functionDef = "fun" id funParameters statementBlock 
funParameters = "(" [ argWithType { "," argWithType } ] ")" ":" type
statement= singleStatement | statementBlock
statementBlock =  "{" { statement  } "}"
singleStatement=  ifStatement |  returnStatement | initStatement | functionDef
ifStatement = "if" "(" condition ")" statement [ "else" statement ]
returnStatement= "return" assignable ";"
initStatement = "val" argWithType assignmentOp assignable  ";"
assignable = expression | anonymousFunction | trueFalse

expression =  ( multiplicativeExpr { additiveOp multiplicativeExpr } )
multiplicativeExpr = primaryExpr { multiplicativeOp primaryExpr }
primaryExpr = ( literal | idReference | parenthExpr |  listExpression )
parenthExpr = "(" expression ")"
listExpression = ( idReference | listLiteral )  { listModifier }
!!! listModifier =  "." ( map | filter | concat ) "("  ( anonymousFunction | idReference)  ")"

condition = andCond { orOp andCond }
andCond = equalityCond { andOp equalityCond }
equalityCond = relationalCond { equalOp relationalCond }
relationalCond = primaryCond { relationOp primaryCond }
primaryCond = [ unaryOp ] ( parenthCond | idReference | numberLiteral )
parenthCond = "(" condition ")"

unaryOp ​= "!" 
assignmentOp​= "=" 
orOp​ = "||" 
andOp ​= "&&" 
equalOp​ = "==" | "!=" 
relationOp = "<" | ">" | "<=" | ">=" 
additiveOp = "+" | "-"  
multiplicativeOp = "*" | "/" | "%" 

literal = numberLiteral | stringLiteral
type = basicType | functionType
basicType = ( Number | String | List | Boolean )
functionType = "Function" "<" "Arg" < [ type  { "," type } ] ">" "," "Returns" "<" type ">" ">"
anonymousFunction = funParameters "=>" statement
idReference = id [ indexingOperator | funCall ] check for indexing and funcall
indexingOperator = [ "[" ( expression ) "]" ]
funCall = "(" [ assignable { "," assignable } ] ")"
numberLiteral = [ "-" ]	finiteNumber 
finiteNumber = digitWithoutZero { digit } [ "." { digit } ]
listLiteral = "[" [  expression { "," ( expression ) } ] "]"
argWithType = id ":" type
id = letter { digit | letter }
letter = "a".."z" | "A".."Z" | "_" | "$" 
digit = (digitWithoutZero | "0")
digitWithoutZero = "1".."9" 
```

## Zwięzły opis rozwiązania
Projekt został podzielony na dwa główne moduły `lexer` i `parser`.
W `lexer` zawarta jest logika czytania znaków z pliku, przetwarzanie tych danych i generowanie ciągu tokenów 
(wszystkie typy tokenów znajdują się pakiecie org.example.compiler.lexer.token).
W `parser` znajduje się cała logika związana z analizą składniową (parsowanie), analizą semantyczną i wykonywaniem 
programu. 
Pakiet org.example.compiler.parser.productions.parsers posiada wszystkie klasy parsujące ciąg znaków.
Pakiem org.example.compiler.parser.productions posiada klasy generowane przez klasy parserów. Klasy te posiadają
funkcje umożliwiające generowanie wyników (funkcja `execute()`)) oraz sprawdzanie semantyczne samych siebie (funckcja
`checkInvariants()`).

## Testowanie
Lexer i parsowanie strumienia zostało przetestowane głównie jednostkowo w wspomnianym wyżej frameworku Spok,
natomiast testowanie wykonywania programu zostało głównie integracyjnie.

## Kompilacja i uruchomienie programu
1. w głównym folderze wpisać komendę 
    ```bash
    ./gradlew shadowJar
    ```
2. w folderze `build/libs` powinien znaleźć się plik o nazwie `compiler-1.0-SNAPSHOT-all.jar`
3. przygotować plik tekstowy (przykładowy znajduje się w główynym folderze - `test.txt`) 
    ```shell script
    cp text.txt ./build/libs
    ```
4. i uruchomić program komendą 

    ```
    java -jar compiler-1.0-SNAPSHOT-all.jar test.txt
    ```
