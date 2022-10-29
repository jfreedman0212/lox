package dev.freedman.jlox;

import java.util.List;

/**
 * Containing all the possible types of issues that can come up during any phase
 * of
 * the interpreter: scanning, parsing, and actual execution issues are all
 * contained in this type.
 * <p>
 * In one way or another, the line in which the error occurred is stored in each
 * record. Sometimes that is directly, other times it's through storing
 * the {@link Token}.
 * </p>
 */
public sealed interface InterpreterIssue {
    public record InvalidCharacter(char c, int line) implements InterpreterIssue {
    }

    public record UnterminatedString(int line) implements InterpreterIssue {
    }

    public record UnterminatedGrouping(Token startingToken) implements InterpreterIssue {
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

    public record ExceededMaximumFunctionArguments(int numberOfArgumentsProvided, int maxArguments, int line)
            implements InterpreterIssue {
    }

    public record ValueNotCallable(Object uncallableObject, Token.RightParenthesis closingParen)
            implements InterpreterIssue {
    }

    public record InvalidNumberOfArguments(int numberOfArgumentsProvided, int numberOfActualArguments,
            Token.RightParenthesis closingParen) implements InterpreterIssue {
    }
}