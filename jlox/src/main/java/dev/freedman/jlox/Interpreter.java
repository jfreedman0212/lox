package dev.freedman.jlox;

import java.util.Objects;

/**
 * Last step of the interpreter, the thing that actually runs the source code!
 * Unlike the other phases that take in a list of things, this phase only takes
 * in a single statement at a time. This allows it to be used for both running
 * a full file and for the REPL.
 */
public class Interpreter {
    private Environment environment;

    public Interpreter() {
        this.environment = new Environment();
    }

    public void execute(final Statement statement) throws InterpreterException {
        if (statement instanceof Statement.Print printStatement) {
            System.out.printf("%s\n", this.executeExpression(printStatement.expression()));
        } else if (statement instanceof Statement.ExpressionStatement expressionStatement) {
            this.executeExpression(expressionStatement.expression());
        } else if (statement instanceof Statement.VariableDeclaration variableDeclaration) {
            final Expression expression = variableDeclaration.expression();
            final Object resolvedValue = Objects.nonNull(expression) ? this.executeExpression(expression) : null;
            environment.declare(variableDeclaration.identifier(), resolvedValue);
        } else if (statement instanceof Statement.Block block) {
            final Environment outerEnvironment = this.environment;
            try {
                this.environment = new Environment(outerEnvironment);
                for (final Statement nestedStatement : block.statements()) {
                    this.execute(nestedStatement);
                }
            } finally {
                this.environment = outerEnvironment;
            }
        } else if (statement instanceof Statement.If ifStatement) {
            final boolean condition = Token.isTruthy(executeExpression(ifStatement.condition()));
            if (condition) {
                execute(ifStatement.thenBranch());
            } else if (Objects.nonNull(ifStatement.elseBranch())) {
                execute(ifStatement.elseBranch());
            }
        }
    }

    public Object executeExpression(final Expression expr) throws InterpreterException {
        if (expr instanceof Expression.Unary unaryExpr) {
            final Object right = executeExpression(unaryExpr.right());
            return unaryExpr.operator().evaluateUnaryOperation(right);
        } else if (expr instanceof Expression.Grouping groupingExpr) {
            return executeExpression(groupingExpr.expression());
        } else if (expr instanceof Expression.Literal literalExpr) {
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
        } else if (expr instanceof Expression.Binary binaryExpr) {
            final Object left = executeExpression(binaryExpr.left());
            final Object right = executeExpression(binaryExpr.right());
            return binaryExpr.operator().evaluateBinaryOperation(left, right);
        } else if (expr instanceof Expression.Variable variable) {
            return environment.retrieve(variable.identifier());
        } else if (expr instanceof Expression.Assignment assignment) {
            final Token.Identifier identifier = assignment.identifier();
            final Object result = this.executeExpression(assignment.assignee());
            environment.assign(identifier, result);
            return result;
        } else if (expr instanceof Expression.Logical logical) {
            final Object left = executeExpression(logical.left());
            if ((logical.operator() instanceof Token.And && !Token.isTruthy(left))
                    || (logical.operator() instanceof Token.Or && Token.isTruthy(left))) {
                return left;
            }
            return executeExpression(logical.right());
        }
        return null;
    }
}
