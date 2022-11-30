package dev.freedman.jlox;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Contains variables for a specific scope. Variables can be accessed from the
 * current scope and any enclosing ones, and they can also be shadowed by lower
 * scopes.
 */
public class Environment {
    private final Map<String, Object> variables;
    private final Environment enclosingEnvironment;

    public Environment(final Environment enclosingEnvironment) {
        this.enclosingEnvironment = enclosingEnvironment;
        this.variables = new HashMap<>();
    }

    public Environment() {
        this(null);
    }

    public void declare(final Token.Identifier identifier, final Object value) throws InterpreterException {
        if (variables.containsKey(identifier.lexeme())) {
            throw new InterpreterException(
                    new InterpreterIssue.VariableAlreadyDefined(identifier.lexeme(), identifier.line()));
        }
        variables.put(identifier.lexeme(), value);
    }

    public void assign(final Token.Identifier identifier, final Object value) throws InterpreterException {
        if (!variables.containsKey(identifier.lexeme())) {
            // if we can't find it AND this is the root environment,
            // the variable does not exist
            if (Objects.isNull(enclosingEnvironment)) {
                throw new InterpreterException(
                        new InterpreterIssue.VariableNotDefined(identifier.lexeme(), identifier.line()));
            }
            // otherwise, search in higher environments for this variable
            enclosingEnvironment.assign(identifier, value);
        }
        variables.put(identifier.lexeme(), value);
    }

    public void assignAt(final int distance, final Token.Identifier identifier, final Object value) {
        final Environment ancestor = ancestor(distance);
        // run this as a sanity check. this shouldn't happen, but better to explicitly
        // handle it (even as something that just blows up) than let it bubble up
        // without any identifying information
        if (!ancestor.variables.containsKey(identifier.lexeme())) {
            throw new RuntimeException(String.format(
                    "Ancestor at depth %d does not contain variable %s used on line %d. This should have been verified by the variable resolution phase, but wasn't.",
                    distance, identifier.lexeme(), identifier.line()));
        }
        ancestor.variables.put(identifier.lexeme(), value);
    }

    public Object retrieve(final Token.Identifier identifier) throws InterpreterException {
        if (!variables.containsKey(identifier.lexeme())) {
            // if we can't find it AND this is the root environment,
            // the variable does not exist
            if (Objects.isNull(enclosingEnvironment)) {
                throw new InterpreterException(
                        new InterpreterIssue.VariableNotDefined(identifier.lexeme(), identifier.line()));
            }
            // otherwise, search in higher environments for this variable
            return enclosingEnvironment.retrieve(identifier);
        }
        return variables.get(identifier.lexeme());
    }

    public Object getAt(final int distance, final Token.Identifier identifier) {
        final Environment ancestor = ancestor(distance);
        // run this as a sanity check. this shouldn't happen, but better to explicitly
        // handle it (even as something that just blows up) than let it bubble up
        // without any identifying information
        if (!ancestor.variables.containsKey(identifier.lexeme())) {
            throw new RuntimeException(String.format(
                    "Ancestor at depth %d does not contain variable %s used on line %d. This should have been verified by the variable resolution phase, but wasn't.",
                    distance, identifier.lexeme(), identifier.line()));
        }
        return ancestor.variables.get(identifier.lexeme());
    }

    private Environment ancestor(final int distance) {
        Environment environment = this;
        for (int i = 0; i < distance; ++i) {
            environment = environment.enclosingEnvironment;
        }
        return environment;
    }
}
