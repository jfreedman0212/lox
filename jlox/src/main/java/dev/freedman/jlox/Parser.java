package dev.freedman.jlox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Takes a list of tokens and produces a list of statements. This represents
 * the second phase in the interpreter.
 * <p>
 * Most functions in this class represent productions in the grammar and are
 * direct translations of them in code-form. Not all of the productions are
 * explicitly
 * created as functions though, I may have inlined some of them.
 * </p>
 */
public class Parser {
    private final List<Token> tokens;
    private final List<InterpreterIssue> issues;
    private int current;

    public Parser(final List<Token> tokens) {
        this.tokens = tokens;
        this.current = 0;
        this.issues = new ArrayList<>();
    }

    public List<Statement> parse() throws InterpreterException {
        final List<Statement> statements = new ArrayList<>();
        while (!isAtEnd()) {
            try {
                statements.add(declaration());
            } catch (final InternalParserException e) {
                // track the issue
                issues.add(e.issue);
                // find the next "statement boundary" to keep going from.
                // this way, we report as many errors to the user as we possibly
                // can. this provides a better user experience than fixing errors
                // one at a time
                synchronize();
            }
        }
        if (!issues.isEmpty()) {
            throw new InterpreterException(issues);
        }
        return statements;
    }

    private Statement declaration() {
        final Token currentToken = tokens.get(current);
        if (currentToken instanceof Token.Var) {
            advance();
            return variableDeclaration();
        }
        return statement();
    }

    private Statement variableDeclaration() {
        Token currentToken = tokens.get(current);
        if (currentToken instanceof Token.Identifier identifier) {
            advance();
            currentToken = tokens.get(current);
            if (currentToken instanceof Token.Equal) {
                advance();
                // from the grammar's standpoint, this doesn't make much sense.
                // I mostly call `expressionStatement` as a convenience to not
                // have to manually check for a terminating semicolon again
                final Statement.ExpressionStatement expressionStmt = expressionStatement();
                return new Statement.VariableDeclaration(identifier, expressionStmt.expression());
            } else if (currentToken instanceof Token.Semicolon) {
                advance();
                return new Statement.VariableDeclaration(identifier, null);
            }
            throw new InternalParserException(new InterpreterIssue.UnexpectedToken(currentToken));
        }
        throw new InternalParserException(new InterpreterIssue.UnexpectedToken(currentToken));
    }

    private Statement statement() {
        final Token currentToken = tokens.get(current);
        if (currentToken instanceof Token.Print printToken) {
            advance();
            final Expression valueToPrint = expression();
            final Token nextToken = tokens.get(current);
            if (nextToken instanceof Token.Semicolon) {
                advance();
                return new Statement.Print(valueToPrint);
            }
            throw new InternalParserException(
                    new InterpreterIssue.UnterminatedStatement(currentToken.line(), printToken));
        } else if (currentToken instanceof Token.LeftBrace starter) {
            advance();
            return new Statement.Block(block(starter));
        } else if (currentToken instanceof Token.If) {
            advance();
            return ifStatement();
        } else if (currentToken instanceof Token.While) {
            advance();
            return whileLoop();
        } else if (currentToken instanceof Token.For) {
            advance();
            return forLoop();
        }
        return expressionStatement();
    }

    private Statement forLoop() {
        // read the opening parenthesis
        final Token potentialOpeningParen = tokens.get(current);
        if (!(potentialOpeningParen instanceof Token.LeftParenthesis)) {
            throw new InternalParserException(new InterpreterIssue.UnexpectedToken(potentialOpeningParen));
        }
        advance();
        // read the initializer expression (if there is one)
        final Statement initializer;
        Token currentToken = tokens.get(current);
        if (currentToken instanceof Token.Semicolon) {
            advance();
            initializer = null;
        } else if (currentToken instanceof Token.Var) {
            advance();
            initializer = variableDeclaration();
        } else {
            initializer = expressionStatement();
        }
        // read the condition expression (if there is one)
        final Expression condition;
        currentToken = tokens.get(current);
        if (currentToken instanceof Token.Semicolon) {
            // uhhhhhhhhhhhhhhhh, this feels weird. I'm saying that this "token"
            // is on the same line as the current one, even though it doesn't exist.
            // this will surely lead to weird error messages about a token that isn't real
            condition = new Expression.Literal(new Token.True("true", currentToken.line()));
        } else {
            condition = expression();
        }
        advance(); // consumes the semicolon
        // read the increment expression (if there is one)
        final Expression increment;
        currentToken = tokens.get(current);
        if (!(currentToken instanceof Token.RightParenthesis)) {
            increment = expression();
        } else {
            increment = null;
        }
        currentToken = tokens.get(current);
        if (!(currentToken instanceof Token.RightParenthesis)) {
            throw new InternalParserException(new InterpreterIssue.UnterminatedGrouping(potentialOpeningParen));
        }
        advance();
        // finally, desugar the for loop into an equivalent while loop
        final Statement loopBody = statement();
        return new Statement.Block(Arrays.asList(
                initializer,
                new Statement.WhileLoop(condition, Objects.nonNull(increment) ? new Statement.Block(Arrays.asList(
                        loopBody,
                        new Statement.ExpressionStatement(increment))) : loopBody)));
    }

    private Statement.WhileLoop whileLoop() {
        final Token potentialOpeningParen = tokens.get(current);
        if (!(potentialOpeningParen instanceof Token.LeftParenthesis)) {
            throw new InternalParserException(new InterpreterIssue.UnexpectedToken(potentialOpeningParen));
        }
        advance();
        final Expression condition = expression();
        final Token potentialClosingParen = tokens.get(current);
        if (!(potentialClosingParen instanceof Token.RightParenthesis)) {
            throw new InternalParserException(new InterpreterIssue.UnterminatedGrouping(potentialOpeningParen));
        }
        advance();
        final Statement loopBody = statement();
        return new Statement.WhileLoop(condition, loopBody);
    }

    private Statement.If ifStatement() {
        final Token potentialOpeningParen = tokens.get(current);
        if (!(potentialOpeningParen instanceof Token.LeftParenthesis)) {
            throw new InternalParserException(new InterpreterIssue.UnexpectedToken(potentialOpeningParen));
        }
        advance();
        final Expression condition = expression();
        final Token potentialClosingParen = tokens.get(current);
        if (!(potentialClosingParen instanceof Token.RightParenthesis)) {
            throw new InternalParserException(new InterpreterIssue.UnterminatedGrouping(potentialOpeningParen));
        }
        advance();
        final Statement thenBranch = statement();
        final Token potentialElse = tokens.get(current);
        final Statement elseBranch;
        if (potentialElse instanceof Token.Else elseToken) {
            advance();
            elseBranch = statement();
        } else {
            elseBranch = null;
        }
        return new Statement.If(condition, thenBranch, elseBranch);
    }

    private List<Statement> block(Token.LeftBrace starter) {
        final List<Statement> innerStatements = new ArrayList<>();
        Token currentToken = tokens.get(current);
        while (!(currentToken instanceof Token.RightBrace) && !isAtEnd()) {
            innerStatements.add(declaration());
            currentToken = tokens.get(current);
        }
        final Token endingToken = tokens.get(current);
        if (endingToken instanceof Token.RightBrace) {
            advance(); // consume the right brace
            return innerStatements;
        }
        throw new InternalParserException(new InterpreterIssue.UnterminatedGrouping(starter));
    }

    private Statement.ExpressionStatement expressionStatement() {
        final Token firstToken = tokens.get(current);
        final Expression expression = expression();
        final Token nextToken = tokens.get(current);
        if (nextToken instanceof Token.Semicolon) {
            advance();
            return new Statement.ExpressionStatement(expression);
        }
        throw new InternalParserException(new InterpreterIssue.UnterminatedStatement(firstToken.line(), firstToken));
    }

    private Expression expression() {
        return assignment();
    }

    private Expression assignment() {
        final Expression expression = or();
        final Token currentToken = tokens.get(current);
        if (currentToken instanceof Token.Equal equals) {
            advance();
            final Expression value = assignment();
            if (expression instanceof Expression.Variable variableDeclaration) {
                return new Expression.Assignment(variableDeclaration.identifier(), value);
            }
            // this doesn't need to bail out of parsing because we haven't gotten into
            // an unworkable state. the syntax itself is fine so far, but it doesn't
            // make any semantic sense. so, we report the error, but continue parsing
            // as if nothing went wrong
            this.issues.add(new InterpreterIssue.InvalidAssignmentTarget(equals));
        }
        return expression;
    }

    private Expression or() {
        Expression expression = and();
        Token currentToken = tokens.get(current);
        while (currentToken instanceof Token.Or orToken) {
            advance();
            final Expression right = and();
            expression = new Expression.Logical(expression, orToken, right);
            currentToken = tokens.get(current);
        }
        return expression;
    }

    private Expression and() {
        Expression expression = equality();
        Token currentToken = tokens.get(current);
        while (currentToken instanceof Token.And andToken) {
            advance();
            final Expression right = equality();
            expression = new Expression.Logical(expression, andToken, right);
            currentToken = tokens.get(current);
        }
        return expression;
    }

    private Expression equality() {
        Expression left = comparison();
        while (true) {
            final Token currentToken = tokens.get(current);
            if (currentToken instanceof Token.BangEqual bangEqual) {
                advance();
                left = new Expression.Binary(left, bangEqual, comparison());
            } else if (currentToken instanceof Token.EqualEqual equalEqual) {
                advance();
                left = new Expression.Binary(left, equalEqual, comparison());
            } else {
                break;
            }
        }
        return left;
    }

    private Expression comparison() {
        Expression left = term();
        while (true) {
            final Token currentToken = tokens.get(current);
            if (currentToken instanceof Token.Less less) {
                advance();
                left = new Expression.Binary(left, less, term());
            } else if (currentToken instanceof Token.LessEqual lessEqual) {
                advance();
                left = new Expression.Binary(left, lessEqual, term());
            } else if (currentToken instanceof Token.Greater greater) {
                advance();
                left = new Expression.Binary(left, greater, term());
            } else if (currentToken instanceof Token.GreaterEqual greaterEqual) {
                advance();
                left = new Expression.Binary(left, greaterEqual, term());
            } else {
                break;
            }
        }
        return left;
    }

    private Expression term() {
        Expression left = factor();
        while (true) {
            final Token currentToken = tokens.get(current);
            if (currentToken instanceof Token.Minus minus) {
                advance();
                left = new Expression.Binary(left, minus, factor());
            } else if (currentToken instanceof Token.Plus plus) {
                advance();
                left = new Expression.Binary(left, plus, factor());
            } else {
                break;
            }
        }
        return left;
    }

    private Expression factor() {
        Expression left = unary();
        while (true) {
            final Token currentToken = tokens.get(current);
            if (currentToken instanceof Token.Slash slash) {
                advance();
                left = new Expression.Binary(left, slash, unary());
            } else if (currentToken instanceof Token.Star star) {
                advance();
                left = new Expression.Binary(left, star, unary());
            } else {
                break;
            }
        }
        return left;
    }

    private Expression unary() {
        final Token currentToken = tokens.get(current);
        if (currentToken instanceof Token.UnaryOperator unaryOperator) {
            advance();
            return new Expression.Unary(unaryOperator, unary());
        } else {
            return primary();
        }
    }

    private Expression primary() {
        final Token currentToken = tokens.get(current);
        if (currentToken instanceof Token.Literal literal) {
            advance();
            return new Expression.Literal(literal);
        }
        if (currentToken instanceof Token.Identifier identifier) {
            advance();
            return new Expression.Variable(identifier);
        }
        if (currentToken instanceof Token.LeftParenthesis leftParenthesis) {
            advance();
            final Expression innerExpression = expression();
            final Token nextToken = tokens.get(current);
            if (nextToken instanceof Token.RightParenthesis) {
                advance();
                return new Expression.Grouping(innerExpression);
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

    private void synchronize() {
        advance();
        while (!isAtEnd()) {
            final Token previousToken = tokens.get(current - 1);
            if (previousToken instanceof Token.Semicolon) {
                return;
            }
            final Token currentToken = tokens.get(current);
            if (currentToken instanceof Token.Class || currentToken instanceof Token.For
                    || currentToken instanceof Token.Fun || currentToken instanceof Token.If
                    || currentToken instanceof Token.Print || currentToken instanceof Token.Return
                    || currentToken instanceof Token.Var || currentToken instanceof Token.While) {
                return;
            }
            advance();
        }
    }

    private static final class InternalParserException extends RuntimeException {
        public final InterpreterIssue issue;

        public InternalParserException(final InterpreterIssue issue) {
            super(issue.toString());
            this.issue = issue;
        }
    }
}
