package dev.freedman.jlox;

import java.util.List;

public sealed interface Stmt {
    public record Print(Expr expression) implements Stmt {
    }

    public record Expression(Expr expression) implements Stmt {
    }

    public record VariableDeclaration(Token.Identifier identifier, Expr expression) implements Stmt {
    }

    public record Block(List<Stmt> statements) implements Stmt {
    }
}
