package dev.freedman.jlox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;

public class VariableResolver {
    private final Interpreter interpreter;
    private final Stack<Map<String, Boolean>> scopes;
    private final List<InterpreterIssue> issues;

    public VariableResolver(final Interpreter interpreter) {
        this.interpreter = interpreter;
        this.scopes = new Stack<>();
        this.issues = new ArrayList<>();
    }

    public void resolveStatements(final List<Statement> statements) throws InterpreterException {
        for (final Statement statement : statements) {
            resolveStatement(statement);
        }
        if (!issues.isEmpty()) {
            throw new InterpreterException(issues);
        }
    }

    private void resolveStatement(final Statement statement) throws InterpreterException {
        if (statement instanceof Statement.Block block) {
            beginScope();
            for (final Statement blockStatement : block.statements()) {
                resolveStatement(blockStatement);
            }
            endScope();
        } else if (statement instanceof Statement.VariableDeclaration variableDeclaration) {
            declare(variableDeclaration.identifier());
            if (Objects.nonNull(variableDeclaration.initializer())) {
                resolveExpression(variableDeclaration.initializer());
            }
            define(variableDeclaration.identifier());
        } else if (statement instanceof Statement.Function function) {
            declare(function.name());
            define(function.name());
            resolveFunction(function);
        } else if (statement instanceof Statement.ExpressionStatement expressionStatement) {
            resolveExpression(expressionStatement.expression());
        } else if (statement instanceof Statement.If ifStatement) {
            resolveExpression(ifStatement.condition());
            resolveStatement(ifStatement.thenBranch());
            if (Objects.nonNull(ifStatement.elseBranch())) {
                resolveStatement(ifStatement.elseBranch());
            }
        } else if (statement instanceof Statement.Print print) {
            resolveExpression(print.expression());
        } else if (statement instanceof Statement.Return returnStatement && Objects.nonNull(returnStatement.value())) {
            resolveExpression(returnStatement.value());
        } else if (statement instanceof Statement.WhileLoop whileLoop) {
            resolveExpression(whileLoop.condition());
            resolveStatement(whileLoop.body());
        } else if (statement instanceof Statement.Assert assertStatement) {
            resolveExpression(assertStatement.expression());
        }
    }

    private void resolveExpression(final Expression expression) throws InterpreterException {
        if (expression instanceof Expression.Variable variable) {
            if (!scopes.isEmpty() && scopes.peek().get(variable.identifier().lexeme()) == Boolean.FALSE) {
                throw new InterpreterException(new InterpreterIssue.VariableNotDefined(variable.identifier().lexeme(),
                        variable.identifier().line()));
            }
            resolveLocal(variable, variable.identifier());
        } else if (expression instanceof Expression.Assignment assignment) {
            resolveExpression(assignment.assignee());
            resolveLocal(assignment, assignment.identifier());
        } else if (expression instanceof Expression.Binary binary) {
            resolveExpression(binary.left());
            resolveExpression(binary.right());
        } else if (expression instanceof Expression.Call call) {
            resolveExpression(call.callee());
            for (final Expression argument : call.arguments()) {
                resolveExpression(argument);
            }
        } else if (expression instanceof Expression.Grouping grouping) {
            resolveExpression(grouping.expression());
        } else if (expression instanceof Expression.Logical logical) {
            resolveExpression(logical.left());
            resolveExpression(logical.right());
        } else if (expression instanceof Expression.Unary unary) {
            resolveExpression(unary.right());
        }
        // skip Literals
    }

    private void resolveFunction(final Statement.Function function) throws InterpreterException {
        beginScope();
        for (final Token.Identifier parameter : function.parameters()) {
            declare(parameter);
            define(parameter);
        }
        resolveStatement(function.body());
        endScope();
    }

    private void resolveLocal(final Expression expression, final Token.Identifier identifier) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(identifier.lexeme())) {
                interpreter.resolve(expression, scopes.size() - 1 - i);
                return;
            }
        }
    }

    private void declare(final Token.Identifier identifier) {
        if (scopes.empty()) {
            return;
        }
        final Map<String, Boolean> scope = scopes.peek();
        scope.put(identifier.lexeme(), false);
    }

    private void define(final Token.Identifier identifier) {
        if (scopes.empty()) {
            return;
        }
        final Map<String, Boolean> scope = scopes.peek();
        scope.put(identifier.lexeme(), true);
    }

    private void beginScope() {
        scopes.push(new HashMap<>());
    }

    private void endScope() {
        scopes.pop();
    }
}
