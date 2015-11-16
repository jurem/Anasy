package anasy.demos.kalq1;

import anasy.parser.SyntaxError;

/**
 * Simple example of calculator-based language. Expressions are immediately calculated during parsing.
 * There is not intermediate representation of code.
 */
public class Kalq1 {

    public static String[] exprs = {
            "1*2+3",
            "1 + 2 * 3",
            "42 + ;; comment\n 3 ;; another comment",
            "3! + 4!",
            " 2 ^ 5    /  4 % 5  * 10 + 3 * 2^2",
            "-3 + 10",
            "-4 - 6",
            "4 < 5"
    };

    public static void main(String[] args) throws SyntaxError {
        System.out.println("Kalq 1 - calculator");
        Parser1 parser = new Parser1();
        try {
            for (String s : exprs)
                System.out.println(s + ": " + parser.parse(s));
        } catch (SyntaxError e) {
            System.err.println(e);
        }
    }

}
