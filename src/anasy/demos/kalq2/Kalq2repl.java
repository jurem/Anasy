package anasy.demos.kalq2;

import anasy.parser.SyntaxError;

public class Kalq2repl {

    public static void main(String[] args) {
        System.out.println("Kalq 2 - calculator language interactive interpreter");
        Parser2 parser = new Parser2(new Engine());
        while (parser.ready()) {
            Engine.Node tree;
            try {
                tree = parser.parse();
                if (tree == null) continue;
            } catch (SyntaxError e) {
                System.err.println(e);
                continue;
            }
            System.out.println("Tree: " + tree);
            // evaluate tree
            Engine.Node result;
            try {
                result = tree.eval();
            } catch (SemanticError e) {
                System.err.println(e);
                continue;
            }
            // print result
            System.out.println(result);
        }
    }

}
