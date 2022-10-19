package dev.freedman.jlox;

import java.util.List;

public class ScannerException extends Exception {
    private final List<ScannerError> errors;

    public ScannerException(final List<ScannerError> errors) {
        super(errors.toString());
        this.errors = errors;
    }

    public List<ScannerError> getErrors() {
        return errors;
    }
}
