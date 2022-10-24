package dev.freedman.jlox;

public sealed interface Stmt {
    public record Print(Expr expression) implements Stmt {
    }

    public record Expression(Expr expression) implements Stmt {
    }

    public record VariableDeclaration(Token.Identifier identifier, Expr expression) implements Stmt {
    }
}
