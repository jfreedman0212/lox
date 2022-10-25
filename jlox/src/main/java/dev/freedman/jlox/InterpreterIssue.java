package dev.freedman.jlox;

import java.util.List;

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

    public record FeatureNotSupportedYet(String featureName, int line) implements InterpreterIssue {
    }

    public record VariableAlreadyDefined(String variableName, int line) implements InterpreterIssue {
    }

    public record VariableNotDefined(String variableName, int line) implements InterpreterIssue {
    }

    public record InvalidAssignmentTarget(Token invalidAssignmentTarget) implements InterpreterIssue {
    }
}
