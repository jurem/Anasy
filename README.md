# Anasy
Operator-precedence parsing of expression-based languages.

Anasy is an object-oriented framework for syntax analysis of text input. The framework is based on the
Pratt parsing algorithm and combines the recursive-descent technique with the operator-precedence parsing.
Specification of operator position as well as operator precedence is straightforwardly supported.
Additionally, new operators and other syntactic constructs can easily be introduced even during the analysis.
This makes the framework very convenient for parsing expression-based languages.

### Features

* easy specification of expression syntax
* support for operator-precedence
* support for prefix, postfix and infix operators
* support for multifix (such as if-then-else-end statements) operators
* support for outfix (such as parentheses) operators
* support for hybrid operators (such as prefix+infix)
* support definition of operators during compile-time
* several examples included

### Project structure

* parser ... main parser libraries
* literals ... literals: identifiers, integers, floats
* whitespace ... classical whitespace skipping, inline comments, indendation (experimental)
* operators ... operators of various fixity
* demos
    * kalq1 ... simple calculator-based language
    * kalq2 ... kalq1 + simple programming language: if statements, function definition and invocation, support definition of new operators in the language
    * kalq3 ... as kalq2 but indendentation based

### Author

Jurij Mihelič<br>
University of Ljubljana<br>
Faculty of Computer and Information Science

Anasy: Operator-precedence parsing of expression-based languages, 2014-2015
