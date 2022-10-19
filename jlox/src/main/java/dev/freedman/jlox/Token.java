package dev.freedman.jlox;

public sealed interface Token {
    // region Single-character tokens
    public record LeftParenthesis(char lexeme, int line) implements Token {
    }

    public record RightParenthesis(char lexeme, int line) implements Token {
    }

    public record LeftBrace(char lexeme, int line) implements Token {
    }

    public record RightBrace(char lexeme, int line) implements Token {
    }

    public record Comma(char lexeme, int line) implements Token {
    }

    public record Dot(char lexeme, int line) implements Token {
    }

    public record Minus(char lexeme, int line) implements Token {
    }

    public record Plus(char lexeme, int line) implements Token {
    }

    public record Semicolon(char lexeme, int line) implements Token {
    }

    public record Slash(char lexeme, int line) implements Token {
    }

    public record Star(char lexeme, int line) implements Token {
    }
    // endregion

    // region One or two character tokens
    public record Bang(char lexeme, int line) implements Token {
    }

    public record BangEqual(String lexeme, int line) implements Token {
    }

    public record Equal(char lexeme, int line) implements Token {
    }

    public record EqualEqual(String lexeme, int line) implements Token {
    }

    public record Greater(char lexeme, int line) implements Token {
    }

    public record GreaterEqual(String lexeme, int line) implements Token {
    }

    public record Less(char lexeme, int line) implements Token {
    }

    public record LessEqual(String lexeme, int line) implements Token {
    }
    // endregion

    // region Literals
    public record Identifier(String lexeme, int line) implements Token {
    }

    public record StringLiteral(String lexeme, int line, String value) implements Token {
    }

    public record Number(String lexeme, int line, double value) implements Token {
    }
    // endregion

    // region Keywords
    public record And(String lexeme, int line) implements Token {
    }

    public record Class(String lexeme, int line) implements Token {
    }

    public record Else(String lexeme, int line) implements Token {
    }

    public record False(String lexeme, int line) implements Token {
    }

    public record Fun(String lexeme, int line) implements Token {
    }

    public record For(String lexeme, int line) implements Token {
    }

    public record If(String lexeme, int line) implements Token {
    }

    public record Nil(String lexeme, int line) implements Token {
    }

    public record Or(String lexeme, int line) implements Token {
    }

    public record Print(String lexeme, int line) implements Token {
    }

    public record Return(String lexeme, int line) implements Token {
    }

    public record Super(String lexeme, int line) implements Token {
    }

    public record This(String lexeme, int line) implements Token {
    }

    public record True(String lexeme, int line) implements Token {
    }

    public record Var(String lexeme, int line) implements Token {
    }

    public record While(String lexeme, int line) implements Token {
    }
    // endregion

    public record EndOfFile(int line) implements Token {
    }
}
