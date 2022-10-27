package dev.freedman.jlox;

public sealed interface Expression {
    public record Binary(Expression left, Token.BinaryOperator operator, Expression right)
            implements Expression {
    }

    public record Grouping(Expression expression) implements Expression {
    }

    public record Literal(Token.Literal value) implements Expression {
    }

    public record Unary(Token.UnaryOperator operator, Expression right) implements Expression {
    }

    public record Variable(Token.Identifier identifier) implements Expression {
    }

    public record Assignment(Token.Identifier identifier, Expression assignee) implements Expression {
    }
}
