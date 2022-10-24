package dev.freedman.jlox;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public sealed interface InterpreterIssue {
    public record InvalidCharacter(char c, int line) implements InterpreterIssue {
    }

    public record UnterminatedString(int line) implements InterpreterIssue {
    }

    public record UnterminatedGrouping(Token.LeftParenthesis startingToken) implements InterpreterIssue {
    }

    public record UnterminatedStatement(int line, Token startingToken) implements InterpreterIssue {
    }

    public record UnexpectedToken(Token token) implements InterpreterIssue {
    }

    public record InvalidTypesForOperation(String operation, List<String> supportedTypes, List<String> receivedValues,
            int line) implements InterpreterIssue {
    }

    public record FeatureNotSupportedYet(String featureName, int line) {
    }
}
