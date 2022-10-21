package dev.freedman.jlox;

import java.util.List;

public class InterpreterException extends Exception {
    private final List<InterpreterIssue> errors;

    public InterpreterException(final List<InterpreterIssue> errors) {
        super(errors.toString());
        this.errors = errors;
    }

    public List<InterpreterIssue> getErrors() {
        return errors;
    }
}
