package dev.freedman.jlox;

public sealed interface Expr {
    public record Binary<Operand, Result> (Expr left, Token.BinaryOperator<Operand, Result> operator, Expr right)
            implements Expr {
    }

    public record Grouping(Expr expression) implements Expr {
    }

    public record Literal(Token.Literal value) implements Expr {
    }

    public record Unary<Operand, Result> (Token.UnaryOperator<Operand, Result> operator, Expr right) implements Expr {
    }
}
