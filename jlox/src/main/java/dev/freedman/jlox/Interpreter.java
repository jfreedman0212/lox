package dev.freedman.jlox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Last step of the interpreter, the thing that actually runs the source code!
 * Unlike the other phases that take in a list of things, this phase only takes
 * in a single statement at a time. This allows it to be used for both running
 * a full file and for the REPL.
 */
public class Interpreter {
    final Environment globals;
    private Environment environment;
    private final Map<Expression, Integer> locals;

    public Interpreter() {
        globals = new Environment();
        environment = globals;
        this.locals = new HashMap<>();
        try {
            globals.declare(new Token.Identifier("clock", 0), new LoxCallable() {
                @Override
                public int arity() {
                    return 0;
                }

                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    final double currentTime = (double) System.currentTimeMillis();
                    return currentTime / 1000.0;
                }

                @Override
                public String toString() {
                    return "<native fun clock>";
                }
            });
        } catch (final InterpreterException e) {
            // convert to an unchecked exception because there's nothing that
            // can be done if this operation fails!
            throw new RuntimeException(e);
        }
    }

    public void execute(final Statement statement) throws InterpreterException {
        if (statement instanceof Statement.Print printStatement) {
            System.out.printf("%s\n", this.executeExpression(printStatement.expression()));
        } else if (statement instanceof Statement.ExpressionStatement expressionStatement) {
            this.executeExpression(expressionStatement.expression());
        } else if (statement instanceof Statement.VariableDeclaration variableDeclaration) {
            final Expression expression = variableDeclaration.initializer();
            final Object resolvedValue = Objects.nonNull(expression) ? this.executeExpression(expression) : null;
            environment.declare(variableDeclaration.identifier(), resolvedValue);
        } else if (statement instanceof Statement.Block block) {
            executeBlock(block, new Environment(environment));
        } else if (statement instanceof Statement.If ifStatement) {
            final boolean condition = Token.isTruthy(executeExpression(ifStatement.condition()));
            if (condition) {
                execute(ifStatement.thenBranch());
            } else if (Objects.nonNull(ifStatement.elseBranch())) {
                execute(ifStatement.elseBranch());
            }
        } else if (statement instanceof Statement.WhileLoop whileLoop) {
            while (Token.isTruthy(executeExpression(whileLoop.condition()))) {
                execute(whileLoop.body());
            }
        } else if (statement instanceof Statement.Function function) {
            environment.declare(function.name(), new LoxFunction(function, environment));
        } else if (statement instanceof Statement.Return returnStatement) {
            final Object value;
            if (Objects.nonNull(returnStatement.value())) {
                value = executeExpression(returnStatement.value());
            } else {
                value = null;
            }
            throw new Return(value, returnStatement.returnKeyword());
        } else if (statement instanceof Statement.Assert assertStatement) {
            final Object value = executeExpression(assertStatement.expression());
            if (!Token.isTruthy(value)) {
                throw new InterpreterException(new InterpreterIssue.AssertionError(assertStatement.assertKeyword(),
                        assertStatement.expression()));
            }
            // otherwise, do nothing
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
            return lookupVariable(variable.identifier(), variable);
        } else if (expr instanceof Expression.Assignment assignment) {
            final Object result = this.executeExpression(assignment.assignee());
            final Integer distance = locals.get(assignment);
            if (Objects.nonNull(distance)) {
                environment.assignAt(distance, assignment.identifier(), result);
            } else {
                globals.assign(assignment.identifier(), result);
            }
            environment.assign(assignment.identifier(), result);
            return result;
        } else if (expr instanceof Expression.Logical logical) {
            final Object left = executeExpression(logical.left());
            if ((logical.operator() instanceof Token.And && !Token.isTruthy(left))
                    || (logical.operator() instanceof Token.Or && Token.isTruthy(left))) {
                return left;
            }
            return executeExpression(logical.right());
        } else if (expr instanceof Expression.Call call) {
            final Object callee = executeExpression(call.callee());
            final List<Object> arguments = new ArrayList<>();
            for (final Expression argument : call.arguments()) {
                arguments.add(this.executeExpression(argument));
            }
            if (callee instanceof LoxCallable loxCallable) {
                if (loxCallable.arity() != arguments.size()) {
                    throw new InterpreterException(new InterpreterIssue.InvalidNumberOfArguments(arguments.size(),
                            loxCallable.arity(), call.closingParen()));
                }
                return loxCallable.call(this, arguments);
            }
            throw new InterpreterException(new InterpreterIssue.ValueNotCallable(callee, call.closingParen()));
        }
        return null;
    }

    public void resolve(final Expression expression, final int depth) {
        locals.put(expression, depth);
    }

    private Object lookupVariable(final Token.Identifier identifier, final Expression expression) throws InterpreterException {
        final Integer distance = locals.get(expression);
        if (Objects.nonNull(distance)) {
            return environment.getAt(distance, identifier);
        }
        return globals.retrieve(identifier);
    }

    void executeBlock(final Statement.Block block, final Environment environment) throws InterpreterException {
        final Environment previous = this.environment;
        try {
            this.environment = environment;
            for (final Statement nestedStatement : block.statements()) {
                this.execute(nestedStatement);
            }
        } finally {
            this.environment = previous;
        }
    }
}
