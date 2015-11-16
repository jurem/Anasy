package anasy.demos.kalq2;

import anasy.parser.SyntaxError;

public class Kalq2 {

    public static String[] exprs = {
            "1 + 2 * 3 - 2^2",
            "a = 42; b = 24; a + b",
            "a = 42; if a == 42 then 1 else 0 end",
            "fun fac(n) is if n == 0 then 1 else n * fac(n-1) end end; a=10; fac(a)",
            "fun fac(n) is\n  if n == 0 then 1 else n * fac(n-1) end\nend\na=10\nfac(a)",
            "fun fib(n) is if n <= 2 then 1 else fib(n-1) + fib(n-2) end end; fib(10)",
            "fun fib(n) is if n <= 2 then 1 else fib(n-1) + fib(n-2) end end; prefix fib 100; fib 10",
            "15 % 10",
            "fun gcd(a, b) is if b == 0 then a else gcd(b, a % b) end end; gcd(15, 10)",
            "fun gcd(a, b) is if b == 0 then a else gcd(b, a % b) end end; gcd(15, 10); infix gcd 40; 15 gcd 10",
    };

    public static void main(String[] args) {
        System.out.println("Kalq 2 - calculator language");
        Parser2 parser = new Parser2(new Engine());
        Engine.Node tree = null;

        for (String s : exprs) {
            // parse string
            try {
                tree = parser.parse(s);
            } catch (SyntaxError e) {
                System.err.println(e);
                System.exit(0);
            }
            System.out.println("Tree: " + tree);

            // evaluate tree
            Engine.Node result = null;
            try {
                result = tree.eval();
            } catch (SemanticError e) {
                System.err.println(e);
                System.exit(0);
            }
            System.out.println(result);
        }
    }

}
