package dev.freedman.jlox;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ScannerTest {
    @Test
    public void Scanner_Smoke_Test() throws InterpreterException {
        // Arrange
        final String sourceCode = """
                    var i = 0;
                    while (i < 20) {
                        print i;
                        i = i + 1;
                    }
                """;
        final Scanner scanner = new Scanner(sourceCode);
        // Act
        final List<Token> actual = scanner.scanTokens();
        // Assert
        final List<Token> expected = Arrays.asList(
                // line 1
                new Token.Var("var", 1),
                new Token.Identifier("i", 1),
                new Token.Equal('=', 1),
                new Token.Number("0", 1, 0),
                new Token.Semicolon(';', 1),
                // line 2
                new Token.While("while", 2),
                new Token.LeftParenthesis('(', 2),
                new Token.Identifier("i", 2),
                new Token.Less('<', 2),
                new Token.Number("20", 2, 20),
                new Token.RightParenthesis(')', 2),
                new Token.LeftBrace('{', 2),
                // line 3
                new Token.Print("print", 3),
                new Token.Identifier("i", 3),
                new Token.Semicolon(';', 3),
                // line 4
                new Token.Identifier("i", 4),
                new Token.Equal('=', 4),
                new Token.Identifier("i", 4),
                new Token.Plus('+', 4),
                new Token.Number("1", 4, 1),
                new Token.Semicolon(';', 4),
                // line 5
                new Token.RightBrace('}', 5),
                // line 6
                new Token.EndOfFile(6));
        Assertions.assertEquals(expected, actual);
    }
}
