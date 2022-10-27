package dev.freedman.jlox;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the first phase of the interpreter: taking the raw text
 * of the source file and breaking it up into "tokens". Tokens are the smallest
 * meaningful piece of data about a piece of source code. From there, we build up
 * our understanding of the code in the file.
 */
public class Scanner {
    private final String source;
    private final List<Token> tokens;
    private final List<InterpreterIssue> errors;
    private int start;
    private int current;
    private int line;

    public Scanner(final String source) {
        this.source = source;
        this.tokens = new ArrayList<>();
        this.errors = new ArrayList<>();
        this.start = 0;
        this.current = 0;
        this.line = 1;
    }

    public List<Token> scanTokens() throws InterpreterException {
        while (!isAtEnd()) {
            start = current;
            final char c = advance();
            switch (c) {
                case '(' -> tokens.add(new Token.LeftParenthesis(c, line));
                case ')' -> tokens.add(new Token.RightParenthesis(c, line));
                case '{' -> tokens.add(new Token.LeftBrace(c, line));
                case '}' -> tokens.add(new Token.RightBrace(c, line));
                case ',' -> tokens.add(new Token.Comma(c, line));
                case '.' -> tokens.add(new Token.Dot(c, line));
                case '-' -> tokens.add(new Token.Minus(c, line));
                case '+' -> tokens.add(new Token.Plus(c, line));
                case ';' -> tokens.add(new Token.Semicolon(c, line));
                case '*' -> tokens.add(new Token.Star(c, line));
                case '!' -> {
                    if (match('=')) {
                        tokens.add(new Token.BangEqual(source.substring(start, current), line));
                    } else {
                        tokens.add(new Token.Bang(c, line));
                    }
                }
                case '=' -> {
                    if (match('=')) {
                        tokens.add(new Token.EqualEqual(source.substring(start, current), line));
                    } else {
                        tokens.add(new Token.Equal(c, line));
                    }
                }
                case '<' -> {
                    if (match('=')) {
                        tokens.add(new Token.LessEqual(source.substring(start, current), line));
                    } else {
                        tokens.add(new Token.Less(c, line));
                    }
                }
                case '>' -> {
                    if (match('=')) {
                        tokens.add(new Token.GreaterEqual(source.substring(start, current), line));
                    } else {
                        tokens.add(new Token.Greater(c, line));
                    }
                }
                case '/' -> {
                    if (match('/')) {
                        // skip a line that contains a comment entirely
                        while (peek() != '\n' && !isAtEnd()) {
                            advance();
                        }
                    } else {
                        tokens.add(new Token.Slash(c, line));
                    }
                }
                case ' ', '\r', '\t' -> {
                    /* ignore whitespace */
                }
                case '\n' -> line++;
                case '"' -> {
                    // try to find the closing quote. if we can't find it,
                    // then the string is unterminated and we have a syntax error.
                    // otherwise, we have a string literal
                    while (peek() != '"' && !isAtEnd()) {
                        if (peek() == '\n') {
                            line++;
                        }
                        advance();
                    }
                    if (isAtEnd()) {
                        errors.add(new InterpreterIssue.UnterminatedString(line));
                    } else {
                        advance();
                        final String literalValue = source.substring(start + 1, current - 1);
                        final String lexeme = source.charAt(start) + literalValue + source.charAt(current - 1);
                        tokens.add(new Token.StringLiteral(lexeme, line, literalValue));
                    }
                }
                default -> {
                    if (isDigit(c)) {
                        while (isDigit(peek())) {
                            advance();
                        }
                        if (peek() == '.' && isDigit(peekNext())) {
                            advance();
                            while (isDigit(peek())) {
                                advance();
                            }
                        }
                        final String lexeme = source.substring(start, current);
                        final double literal = Double.parseDouble(lexeme);
                        tokens.add(new Token.Number(lexeme, line, literal));
                    } else if (isAlpha(c)) {
                        while (isAlphaNumeric(peek())) {
                            advance();
                        }
                        final String lexeme = source.substring(start, current);
                        final Token token = switch (lexeme) {
                            case "and" -> new Token.And(lexeme, line);
                            case "class" -> new Token.Class(lexeme, line);
                            case "else" -> new Token.Else(lexeme, line);
                            case "false" -> new Token.False(lexeme, line);
                            case "for" -> new Token.For(lexeme, line);
                            case "fun" -> new Token.Fun(lexeme, line);
                            case "if" -> new Token.If(lexeme, line);
                            case "nil" -> new Token.Nil(lexeme, line);
                            case "or" -> new Token.Or(lexeme, line);
                            case "print" -> new Token.Print(lexeme, line);
                            case "return" -> new Token.Return(lexeme, line);
                            case "super" -> new Token.Super(lexeme, line);
                            case "this" -> new Token.This(lexeme, line);
                            case "true" -> new Token.True(lexeme, line);
                            case "var" -> new Token.Var(lexeme, line);
                            case "while" -> new Token.While(lexeme, line);
                            default -> new Token.Identifier(lexeme, line);
                        };
                        tokens.add(token);
                    } else {
                        errors.add(new InterpreterIssue.InvalidCharacter(c, line));
                    }
                }
            }
        }
        tokens.add(new Token.EndOfFile(line));
        if (!errors.isEmpty()) {
            throw new InterpreterException(errors);
        }
        return tokens;
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z')
                || (c >= 'A' && c <= 'Z')
                || c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private char peekNext() {
        if (current + 1 >= source.length()) {
            return '\0';
        }
        return source.charAt(current + 1);
    }

    private boolean isDigit(final char c) {
        return c >= '0' && c <= '9';
    }

    private boolean match(final char expected) {
        if (isAtEnd()) {
            return false;
        }
        if (source.charAt(current) != expected) {
            return false;
        }
        current++;
        return true;
    }

    private char peek() {
        if (isAtEnd()) {
            return '\0';
        }
        return source.charAt(current);
    }

    private char advance() {
        return source.charAt(current++);
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }
}
