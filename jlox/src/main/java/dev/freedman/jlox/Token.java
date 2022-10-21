package dev.freedman.jlox;

public sealed interface Token {
    public sealed interface Literal extends Token {
    }

    public sealed interface BinaryOperator extends Token {
    }

    public sealed interface UnaryOperator extends Token {
    }

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

    public record Minus(char lexeme, int line) implements BinaryOperator, UnaryOperator {
    }

    public record Plus(char lexeme, int line) implements BinaryOperator {
    }

    public record Semicolon(char lexeme, int line) implements Token {
    }

    public record Slash(char lexeme, int line) implements BinaryOperator {
    }

    public record Star(char lexeme, int line) implements BinaryOperator {
    }
    // endregion

    // region One or two character tokens
    public record Bang(char lexeme, int line) implements UnaryOperator {
    }

    public record BangEqual(String lexeme, int line) implements BinaryOperator {
    }

    public record Equal(char lexeme, int line) implements Token {
    }

    public record EqualEqual(String lexeme, int line) implements BinaryOperator {
    }

    public record Greater(char lexeme, int line) implements BinaryOperator {
    }

    public record GreaterEqual(String lexeme, int line) implements BinaryOperator {
    }

    public record Less(char lexeme, int line) implements BinaryOperator {
    }

    public record LessEqual(String lexeme, int line) implements BinaryOperator {
    }
    // endregion

    // region Literals
    public record Identifier(String lexeme, int line) implements Literal {
    }

    public record StringLiteral(String lexeme, int line, String value) implements Literal {
    }

    public record Number(String lexeme, int line, double value) implements Literal {
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

    public record Nil(String lexeme, int line) implements Literal {
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
