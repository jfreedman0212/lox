package dev.freedman.jlox;

import java.util.List;

public sealed interface Statement {
    public record Print(Expression expression) implements Statement {
    }

    public record ExpressionStatement(Expression expression) implements Statement {
    }

    public record VariableDeclaration(Token.Identifier identifier, Expression expression) implements Statement {
    }

    public record Block(List<Statement> statements) implements Statement {
    }

    public record If(Expression condition, Statement thenBranch, Statement elseBranch) implements Statement {
    }
}
