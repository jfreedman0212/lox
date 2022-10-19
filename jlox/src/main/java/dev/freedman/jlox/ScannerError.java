package dev.freedman.jlox;

public sealed interface ScannerError {
    public record InvalidCharacter(char c, int line) implements ScannerError {
    }

    public record UnterminatedString(int line) implements ScannerError {
    }
}
