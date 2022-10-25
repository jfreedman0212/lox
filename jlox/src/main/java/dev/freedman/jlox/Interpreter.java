package dev.freedman.jlox;

import java.util.Objects;

public class Interpreter {
    private final Environment environment;

    public Interpreter() {
        this.environment = new Environment();
    }

    public void execute(final Stmt statement) throws InterpreterException {
        if (statement instanceof Stmt.Print printStatement) {
            System.out.printf("%s\n", this.executeExpression(printStatement.expression()));
        } else if (statement instanceof Stmt.Expression expressionStatement) {
            this.executeExpression(expressionStatement.expression());
        } else if (statement instanceof Stmt.VariableDeclaration variableDeclaration) {
            final Expr expression = variableDeclaration.expression();
            final Object resolvedValue = Objects.nonNull(expression) ? this.executeExpression(expression) : null;
            environment.declare(variableDeclaration.identifier(), resolvedValue);
        }
    }

    public Object executeExpression(final Expr expr) throws InterpreterException {
        if (expr instanceof Expr.Unary unaryExpr) {
            final Object right = executeExpression(unaryExpr.right());
            return unaryExpr.operator().evaluateUnaryOperation(right);
        } else if (expr instanceof Expr.Grouping groupingExpr) {
            return executeExpression(groupingExpr.expression());
        } else if (expr instanceof Expr.Literal literalExpr) {
            final Token.Literal literal = literalExpr.value();
            if (literal instanceof Token.Nil) {
                return null;
            } else if (literal instanceof Token.Number number) {
                return number.value();
            } else if (literal instanceof Token.StringLiteral string) {
                return string.value();
            } else if (literal instanceof Token.True) {
                return true;
            } else if (literal instanceof Token.False) {
                return false;
            }
        } else if (expr instanceof Expr.Binary binaryExpr) {
            final Object left = executeExpression(binaryExpr.left());
            final Object right = executeExpression(binaryExpr.right());
            return binaryExpr.operator().evaluateBinaryOperation(left, right);
        } else if (expr instanceof Expr.Variable variable) {
            return environment.retrieve(variable.identifier());
        } else if (expr instanceof Expr.Assignment assignment) {
            final Token.Identifier identifier = assignment.identifier();
            final Object result = this.executeExpression(assignment.assignee());
            environment.assign(identifier, result);
            return result;
        }
        return null;
    }
}
