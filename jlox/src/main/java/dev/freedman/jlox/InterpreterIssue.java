package dev.freedman.jlox;

public sealed interface InterpreterIssue {
    public record InvalidCharacter(char c, int line) implements InterpreterIssue {
        @Override
        public String toString() {
            return String.format("Line %d: Invalid character %c\n", line, c);
        }
    }

    public record UnterminatedString(int line) implements InterpreterIssue {
        @Override
        public String toString() {
            return String.format("Line %d: Unterminated string %c\n", line);
        }
    }

    public record UnterminatedGrouping(Token.LeftParenthesis startingToken) implements InterpreterIssue {
        @Override
        public String toString() {
            return String.format("Line %d: Unterminated grouping that begins with \"%s\"\n", startingToken.line(),
                    startingToken.lexeme());
        }
    }

    public record UnexpectedToken(Token token) implements InterpreterIssue {
    }
}
