package anasy.demos.kalq1;

import anasy.literals.RealNum;
import anasy.operators.Infix;
import anasy.operators.Postfix;
import anasy.operators.Prefix;
import anasy.operators.PrefixInfix;
import anasy.parser.InteractiveParser;
import anasy.whitespace.InlineComment;
import anasy.whitespace.Whitespace;


/**
 * Simple calculator based programming language.
 * Included operators:<br><ul>
 * <li>infix comparisons: ==, !=, <=, <, ==, >=, ></li>
 * <li>infix: +, -, *, /, %, ^</li>
 * <li>postfix: ! (factorial)</li>
 * <li>prefix: - (unary), @ (hashcode)</li>
 * </ul>
 */
public class Parser1 extends InteractiveParser<Double> {

    @Override
    protected boolean canRefeed(int stage) {
        return curr == null && queryState >= 0 && stage == 0 && depth > 1;
    }

    @Override
    protected void init() {
        new Whitespace<Double>(this).setLevel(0);
        new InlineComment<Double>(this, ";;").setLevel(0);

        new RealNum<Double>(this, "<num>") {
            @Override
            public Double makeNode(String lexeme) {
                return Double.parseDouble(lexeme);
            }
        };

        new Infix<Double>(this, "==", 40) {
            @Override
            public Double makeInfixNode(Double left, Double right) {
                return left.doubleValue() == right.doubleValue() ? 1.0 : 0.0;
            }
        };

        new Infix<Double>(this, "!=", 40) {
            @Override
            public Double makeInfixNode(Double left, Double right) {
                return left != right ? 1.0 : 0.0;
            }
        };

        new Infix<Double>(this, "<=", 40) {
            @Override
            public Double makeInfixNode(Double left, Double right) {
                return left <= right ? 1.0 : 0.0;
            }
        };

        new Infix<Double>(this, "<", 40) {
            @Override
            public Double makeInfixNode(Double left, Double right) {
                return left < right ? 1.0 : 0.0;
            }
        };

        new Infix<Double>(this, ">=", 40) {
            @Override
            public Double makeInfixNode(Double left, Double right) {
                return left >= right ? 1.0 : 0.0;
            }
        };

        new Infix<Double>(this, ">", 40) {
            @Override
            public Double makeInfixNode(Double left, Double right) {
                return left > right ? 1.0 : 0.0;
            }
        };

        new PrefixInfix<Double>(this, "+", 100, 50) {
            @Override
            public Double makePrefixNode(Double operand) {
                return operand;
            }

            @Override
            public Double makeInfixNode(Double left, Double right) {
                return left + right;
            }
        };

        new PrefixInfix<Double>(this, "-", 100, 50) {
            @Override
            public Double makePrefixNode(Double operand) {
                return -operand;
            }

            @Override
            public Double makeInfixNode(Double left, Double right) {
                return left - right;
            }
        };

        new Infix<Double>(this, "*", 60) {
            @Override
            public Double makeInfixNode(Double left, Double right) {
                return left * right;
            }
        };

        new Infix<Double>(this, "/", 60) {
            @Override
            public Double makeInfixNode(Double left, Double right) {
                return left / right;
            }
        };

        new Infix<Double>(this, "%", 60) {
            @Override
            public Double makeInfixNode(Double left, Double right) {
                return left % right;
            }
        };

        new Infix<Double>(this, "^", 70, true) {
            @Override
            public Double makeInfixNode(Double left, Double right) {
                return Math.pow(left, right);
            }
        };

        new Postfix<Double>(this, "!", 80) {
            @Override
            public Double makePostfixNode(Double operand) {
                double n = operand, r = 1;
                while (n > 1) r *= n--;
                return r;
            }
        };

        new Prefix<Double>(this, "@", 80) {
            @Override
            public Double makePrefixNode(Double operand) {
                return (double) operand.hashCode();
            }
        };
    }

}