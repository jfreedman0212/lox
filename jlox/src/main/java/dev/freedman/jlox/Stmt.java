package dev.freedman.jlox;

public sealed interface Stmt {
    public record Print(Token.Print printToken, Expr expression, Token.Semicolon semicolon) implements Stmt {
    }

    public record Expression(Expr expression, Token.Semicolon semicolon) implements Stmt {
    }
}
