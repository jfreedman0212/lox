package dev.freedman.jlox;

public sealed interface Expr {
    public record Binary(Expr left, Token.BinaryOperator operator, Expr right) implements Expr {
    }

    public record Grouping(Expr expression) implements Expr {
    }

    public record Literal(Token.Literal value) implements Expr {
    }

    public record Unary(Token.UnaryOperator operator, Expr right) implements Expr {
    }
}
