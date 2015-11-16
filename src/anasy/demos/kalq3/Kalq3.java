package anasy.demos.kalq3;


import anasy.demos.kalq3.Engine.Node;
import anasy.parser.SyntaxError;


public class Kalq3 {

    public static void main(String[] args) {
        System.out.println("Kalq 3 - calculator language interactive interpreter");
        Parser3 parser = new Parser3(new Engine());
        while (parser.ready()) {
            Node tree;
            try {
                tree = parser.parse();
                if (tree == null) continue;
            } catch (SyntaxError e) {
                System.err.println(e);
                continue;
            }
            System.out.println("Tree: " + tree);
            // evaluate tree
            Node result;
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
