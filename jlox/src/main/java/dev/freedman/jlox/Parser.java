package dev.freedman.jlox;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private int current;

    public Parser(final List<Token> tokens) {
        this.tokens = tokens;
        this.current = 0;
    }

    public List<Stmt> parse() throws InterpreterException {
        final List<Stmt> statements = new ArrayList<>();
        while (!isAtEnd()) {
            try {
                statements.add(declaration());
            } catch (final InternalParserException e) {
                throw new InterpreterException(e.issue);
            }
        }
        return statements;
    }

    private Stmt declaration() {
        final Token currentToken = tokens.get(current);
        if (currentToken instanceof Token.Var) {
            advance();
            return variableDeclaration();
        }
        return statement();
    }

    private Stmt variableDeclaration() {
        Token currentToken = tokens.get(current);
        if (currentToken instanceof Token.Identifier identifier) {
            advance();
            currentToken = tokens.get(current);
            if (currentToken instanceof Token.Equal) {
                advance();
                // from the grammar's standpoint, this doesn't make much sense.
                // I mostly call `expressionStatement` as a convenience to not
                // have to manually check for a terminating semicolon again
                final Stmt.Expression expressionStmt = expressionStatement();
                return new Stmt.VariableDeclaration(identifier, expressionStmt.expression());
            } else if (currentToken instanceof Token.Semicolon) {
                advance();
                return new Stmt.VariableDeclaration(identifier, null);
            }
            throw new InternalParserException(new InterpreterIssue.UnexpectedToken(currentToken));
        }
        throw new InternalParserException(new InterpreterIssue.UnexpectedToken(currentToken));
    }

    private Stmt statement() {
        final Token currentToken = tokens.get(current);
        if (currentToken instanceof Token.Print printToken) {
            advance();
            final Expr valueToPrint = expression();
            final Token nextToken = tokens.get(current);
            if (nextToken instanceof Token.Semicolon) {
                advance();
                return new Stmt.Print(valueToPrint);
            }
            throw new InternalParserException(
                    new InterpreterIssue.UnterminatedStatement(currentToken.line(), printToken));
        }
        return expressionStatement();
    }

    private Stmt.Expression expressionStatement() {
        final Token firstToken = tokens.get(current);
        final Expr expression = expression();
        final Token nextToken = tokens.get(current);
        if (nextToken instanceof Token.Semicolon) {
            advance();
            return new Stmt.Expression(expression);
        }
        throw new InternalParserException(new InterpreterIssue.UnterminatedStatement(firstToken.line(), firstToken));
    }

    private Expr expression() {
        return equality();
    }

    private Expr equality() {
        Expr left = comparison();
        while (true) {
            final Token currentToken = tokens.get(current);
            if (currentToken instanceof Token.BangEqual bangEqual) {
                advance();
                left = new Expr.Binary(left, bangEqual, comparison());
            } else if (currentToken instanceof Token.EqualEqual equalEqual) {
                advance();
                left = new Expr.Binary(left, equalEqual, comparison());
            } else {
                break;
            }
        }
        return left;
    }

    private Expr comparison() {
        Expr left = term();
        while (true) {
            final Token currentToken = tokens.get(current);
            if (currentToken instanceof Token.Less less) {
                advance();
                left = new Expr.Binary(left, less, term());
            } else if (currentToken instanceof Token.LessEqual lessEqual) {
                advance();
                left = new Expr.Binary(left, lessEqual, term());
            } else if (currentToken instanceof Token.Greater greater) {
                advance();
                left = new Expr.Binary(left, greater, term());
            } else if (currentToken instanceof Token.GreaterEqual greaterEqual) {
                advance();
                left = new Expr.Binary(left, greaterEqual, term());
            } else {
                break;
            }
        }
        return left;
    }

    private Expr term() {
        Expr left = factor();
        while (true) {
            final Token currentToken = tokens.get(current);
            if (currentToken instanceof Token.Minus minus) {
                advance();
                left = new Expr.Binary(left, minus, factor());
            } else if (currentToken instanceof Token.Plus plus) {
                advance();
                left = new Expr.Binary(left, plus, factor());
            } else {
                break;
            }
        }
        return left;
    }

    private Expr factor() {
        Expr left = unary();
        while (true) {
            final Token currentToken = tokens.get(current);
            if (currentToken instanceof Token.Slash slash) {
                advance();
                left = new Expr.Binary(left, slash, unary());
            } else if (currentToken instanceof Token.Star star) {
                advance();
                left = new Expr.Binary(left, star, unary());
            } else {
                break;
            }
        }
        return left;
    }

    private Expr unary() {
        final Token currentToken = tokens.get(current);
        if (currentToken instanceof Token.UnaryOperator unaryOperator) {
            advance();
            return new Expr.Unary(unaryOperator, unary());
        } else {
            return primary();
        }
    }

    private Expr primary() {
        final Token currentToken = tokens.get(current);
        if (currentToken instanceof Token.Literal literal) {
            advance();
            return new Expr.Literal(literal);
        }
        if (currentToken instanceof Token.LeftParenthesis leftParenthesis) {
            advance();
            final Expr innerExpression = expression();
            final Token nextToken = tokens.get(current);
            if (nextToken instanceof Token.RightParenthesis) {
                advance();
                return new Expr.Grouping(innerExpression);
            }
            throw new InternalParserException(new InterpreterIssue.UnterminatedGrouping(leftParenthesis));
        }
        throw new InternalParserException(new InterpreterIssue.UnexpectedToken(currentToken));
    }

    private Token advance() {
        if (!isAtEnd()) {
            current++;
        }
        return tokens.get(current - 1);
    }

    private boolean isAtEnd() {
        return tokens.get(current) instanceof Token.EndOfFile;
    }

    private static final class InternalParserException extends RuntimeException {
        public final InterpreterIssue issue;

        public InternalParserException(final InterpreterIssue issue) {
            super(issue.toString());
            this.issue = issue;
        }
    }
}
