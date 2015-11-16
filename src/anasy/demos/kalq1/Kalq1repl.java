package anasy.demos.kalq1;

import anasy.parser.SyntaxError;


public class Kalq1repl {

    public static void main(String[] args) throws SyntaxError {
        System.out.println("Kalq 1 - calculator interactive interpreter");
        Parser1 parser = new Parser1();
        while (parser.ready()) {
            try {
                Double result = parser.parse();
                if (result == null) continue;
                System.out.println(result);
            } catch (SyntaxError e) {
                System.err.println(e);
                continue;
            }
        }
    }

}
