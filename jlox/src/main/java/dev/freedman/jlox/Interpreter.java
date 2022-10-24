package dev.freedman.jlox;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class Interpreter {
    private final Map<String, Object> variables;

    public Interpreter() {
        this.variables = new HashMap<>();
    }

    public void execute(final Stmt statement) throws InterpreterException {
        if (statement instanceof Stmt.Print printStatement) {
            System.out.printf("%s\n", this.executeExpression(printStatement.expression()));
        } else if (statement instanceof Stmt.Expression expressionStatement) {
            this.executeExpression(expressionStatement.expression());
        } else if (statement instanceof Stmt.VariableDeclaration variableDeclaration) {
            final String variableName = variableDeclaration.identifier().lexeme();
            if (variables.containsKey(variableName)) {
                throw new InterpreterException(new InterpreterIssue.VariableAlreadyDefined(variableName,
                        variableDeclaration.identifier().line()));
            }
            final Expr expression = variableDeclaration.expression();
            if (Objects.nonNull(expression)) {
                final Object result = this.executeExpression(expression);
                variables.put(variableName, result);
            } else {
                variables.put(variableName, null);
            }
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
            if (literal instanceof Token.Identifier identifier) {
                if (!variables.containsKey(identifier.lexeme())) {
                    throw new InterpreterException(
                            new InterpreterIssue.VariableNotDefined(identifier.lexeme(), identifier.line()));
                }
                return variables.get(identifier.lexeme());
            } else if (literal instanceof Token.Nil) {
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
        }
        return null;
    }
}
