package anasy.demos.kalq3;

import anasy.demos.kalq3.Engine.Composite;
import anasy.demos.kalq3.Engine.Node;
import anasy.demos.kalq3.Engine.Num;
import anasy.demos.kalq3.Engine.Sym;
import anasy.literals.Name;
import anasy.literals.RealNum;
import anasy.operators.*;
import anasy.parser.*;
import anasy.whitespace.Indenter;

import java.util.ArrayList;
import java.util.List;


/**
 * Indendation based programming language. Experimental.
 */
public class Parser3 extends InteractiveParser<Node> {

    protected Engine engine;
    protected Indenter<Node> indenter;

    public Parser3(Engine engine) {
        this.engine = engine;
    }

    @Override
    protected boolean canRefeed(int stage) {
        boolean before = stage == 0;
        return curr == null && (before && queryState >= 1 || !before && indenter.getIndentationLevel() > 1);
    }

    public Node expressionIndent(int rbp) throws SyntaxError {
        queryState++;
        boolean indented = advanceIf(indenter.tokIndent) != null;
        queryState--;
        if (indented) rbp = 0;
        Node node = expression(rbp);
        if (indented) advance(indenter.tokDedent);
        return node;
    }

    public void init() {
//		new Whitespace().setLevel(0);
        indenter = new Indenter<Node>(this);
        indenter.setLevel(0);
        indenter.tokNewline = new TokInfix(this, ";", 10);


        new RealNum<Node>(this, "<num>") {
            @Override
            public Node makeNode(String lexeme) {
                return engine.new Num(Double.parseDouble(lexeme));
            }
        }.setLevel(20);

        new Name<Node>(this, "<name>") {
            @Override
            public Node makeNode(String lexeme) {
                return engine.makeSym(lexeme);
            }
        }.setLevel(50);

        new TokParentheses(this);

        new TokInfix(this, ";", 10);

        new TokFun(this, 20);

        new TokIf(this, 30);

        new TokInfix(this, "==", 40);
        new TokInfix(this, "=", 20);
        new TokInfix(this, "!=", 40);
        new TokInfix(this, "<=", 40);
        new TokInfix(this, "<", 40);
        new TokInfix(this, ">=", 40);
        new TokInfix(this, ">", 40);
        new TokPrefixInfix(this, "+", 100, 50);
        new TokPrefixInfix(this, "-", 100, 50);
        new TokInfix(this, "*", 60);
        new TokInfix(this, "/", 60);
        new TokInfix(this, "%", 60);
        new TokInfix(this, "^", 70, true);

        new TokPrefixPostfix(this, "++", 80, 80);
        new TokPrefixPostfix(this, "--", 80, 80);

        new Postfix<Node>(this, "!", 80) {
            @Override
            public Node makePostfixNode(Node operand) {
                return engine.new UnaryOp(name, operand);
            }
        };

        new Prefix<Node>(this, "@", 80) {
            @Override
            public Node makePrefixNode(Node operand) {
                return engine.new UnaryOp(name, operand);
            }
        };

        new Operator<Node>(this, "prefix", 0) {
            @Override
            public Node parse(Token<Node> token) throws SyntaxError {
                // prefix name pbp
                final Sym sym = (Sym) expression(1000, Sym.class);
                Num pbp = (Num) expression(1000, Num.class);
                new Prefix<Node>(parser, sym.val, (int) pbp.val) {
                    @Override
                    public Node makePrefixNode(Node operand) {
                        Node[] args = {operand};
                        return engine.new Composite(sym, args);
                    }
                };
                return engine.new Num(0);
            }
        };

        new Operator<Node>(this, "infix", 0) {
            @Override
            public Node parse(Token<Node> token) throws SyntaxError {
                // infix name lbp
                final Sym sym = (Sym) expression(1000, Sym.class);
                Num lbp = (Num) expression(1000, Num.class);
                new Infix<Node>(parser, sym.val, (int) lbp.val) {
                    @Override
                    public Node makeInfixNode(Node left, Node right) {
                        Node[] args = {left, right};
                        return engine.new Composite(sym, args);
                    }
                };
                return engine.new Num(0);
            }
        };
    }

    class TokInfix extends Infix<Node> {
        public TokInfix(Parser<Node> parser, String name, int lbp) {
            super(parser, name, lbp);
        }

        public TokInfix(Parser<Node> parser, String name, int lbp, boolean rightAssoc) {
            super(parser, name, lbp, rightAssoc);
        }

        @Override
        public Node makeInfixNode(Node left, Node right) {
            return engine.new BinOp(name, left, right);
        }
    }

    class TokPrefixInfix extends PrefixInfix<Node> {
        public TokPrefixInfix(Parser<Node> parser, String name, int pbp, int lbp) {
            super(parser, name, pbp, lbp);
        }

        @Override
        public Node makePrefixNode(Node operand) {
            return engine.new UnaryOp(name, operand);
        }

        @Override
        public Node makeInfixNode(Node left, Node right) {
            return engine.new BinOp(name, left, right);
        }
    }

    class TokPrefixPostfix extends PrefixPostfix<Node> {
        public TokPrefixPostfix(Parser<Node> parser, String name, int pbp, int lbp) {
            super(parser, name, pbp, lbp);
        }

        @Override
        public Node makePrefixNode(Node operand) {
            return engine.new UnaryOp(name, operand);
        }

        @Override
        public Node makePostfixNode(Node operand) {
            return engine.new UnaryOp(name, operand);
        }
    }

    public class TokIf extends Operator<Node> {
        private Tokind<Node> tokThen;
        private Tokind<Node> tokElse;
        private Tokind<Node> tokEnd;

        public TokIf(Parser<Node> parser, int ifbp) {
            super(parser, "if", ifbp);
            tokThen = Operator.make(parser, "then", 0);
            tokElse = Operator.make(parser, "else", 0);
            tokEnd = Operator.make(parser, "end", 0);
        }

        public Node makeIfNode(Node head, Node body, Node tail) {
            return engine.new Branch(head, body, tail);
        }

        @Override
        public Node parse(Token<Node> token) throws SyntaxError {
            // prefix form: if Head then Body else Tail end
            // head
            Node head = expression(lbp);
            // then E
            advance(tokThen);
            Node body = expressionIndent(tokThen.lbp());
            // else E
            advance(tokElse);
            Node tail = expressionIndent(tokElse.lbp());
            // end
            advanceIf(tokEnd);
            return makeIfNode(head, body, tail);
        }

        @Override
        public Node parse(Token<Node> token, Node left) throws SyntaxError {
            // infix form: Body if Head else Tail
            Node head = expression(lbp);
            advance(tokElse);
            Node tail = expression(tokElse.lbp());
            return makeIfNode(head, left, tail);
        }
    }

    class TokParentheses extends Outfix<Node> {
        Tokind<Node> tokComma;

        public TokParentheses(Parser<Node> parser) {
            super(parser, "(", ")", 0);
            this.lbp = 200;
            this.tokComma = Operator.make(parser, ",", 0);
        }

        @Override
        public Node parse(Token<Node> token, Node left) throws SyntaxError {
            if (!(left instanceof Engine.Sym))
                throw new SyntaxError("Function name expected", input.getLoc());
            // function call or definition
            List<Node> exprs = new ArrayList<Node>();
            if (advanceIf(tokClose) == null)
                do
                    exprs.add(expression(0));
                while (advanceIf(tokComma) != null);
            advance(tokClose);
            return engine.new Composite((Engine.Sym) left, exprs.toArray(new Node[exprs.size()]));
        }
    }

    class TokFun extends Operator<Node> {
        private Tokind<Node> tokIs;
        private Tokind<Node> tokEnd;

        public TokFun(Parser<Node> parser, int lbp) {
            super(parser, "fun", lbp);
            this.tokIs = Operator.make(parser, "is", 0);
            this.tokEnd = Operator.make(parser, "end", 0);
        }

        @Override
        public Node parse(Token<Node> token) throws SyntaxError {
            Node node = expression(0);
            if (!(node instanceof Composite))
                throw new SyntaxError("Expected argument list", input.getLoc());
            // parser is and body of the function
            advance(tokIs);
            Node body = expressionIndent(0);
            // end
            advanceIf(tokEnd);
            //
            Composite c = (Composite) node;
            return engine.new Func(c.head, c.args, body);
        }
    }

}