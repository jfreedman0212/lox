package dev.freedman.jlox;

import java.util.Collections;
import java.util.List;

public class InterpreterException extends Exception {
    private final List<InterpreterIssue> errors;

    public InterpreterException(final List<InterpreterIssue> errors) {
        super(errors.toString());
        this.errors = errors;
    }

    public InterpreterException(final InterpreterIssue error) {
        this(Collections.singletonList(error));
    }

    public List<InterpreterIssue> getErrors() {
        return errors;
    }
}
