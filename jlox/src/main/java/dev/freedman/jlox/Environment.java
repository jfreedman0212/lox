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
}
