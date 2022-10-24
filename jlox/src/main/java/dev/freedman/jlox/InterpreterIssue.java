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
        @Override
        public String toString() {
            final List<String> supportedTypes = Objects.requireNonNullElse(this.supportedTypes,
                    Collections.emptyList());
            final List<String> receivedValues = Objects.requireNonNullElse(this.receivedValues,
                    Collections.emptyList());

            if (supportedTypes.isEmpty() && receivedValues.isEmpty()) {
                return String.format("Line %d: %s was not called with the correct types", line, operation);
            }

            final StringBuilder supportedTypesBuilder = new StringBuilder(" expected");
            if (supportedTypes.size() == 1) {
                supportedTypesBuilder.append(" type ");
                supportedTypesBuilder.append(supportedTypes.get(0));
            } else if (supportedTypes.size() > 1) {
                supportedTypesBuilder.append(" types ");
                for (int i = 0; i < supportedTypes.size(); ++i) {
                    supportedTypesBuilder.append(supportedTypes.get(i));
                    if (i < supportedTypes.size() - 1) {
                        supportedTypesBuilder.append(", ");
                    }
                    if (i == supportedTypes.size() - 2) {
                        supportedTypesBuilder.append("and ");
                    }
                }
            }

            final StringBuilder receivedValuesBuilder = new StringBuilder(" instead got ");
            if (receivedValues.size() == 1) {
                receivedValuesBuilder.append(receivedValues.get(0));
            } else if (receivedValues.size() > 1) {
                for (int i = 0; i < receivedValues.size(); ++i) {
                    receivedValuesBuilder.append(receivedValues.get(i));
                    if (i < receivedValues.size() - 1) {
                        receivedValuesBuilder.append(", ");
                    }
                    if (i == receivedValues.size() - 2) {
                        receivedValuesBuilder.append("and ");
                    }
                }
            }
            return String.format("Line %d: %s %s%s", line, operation, supportedTypesBuilder.toString(),
                    receivedValuesBuilder.toString());
        }
    }

    public record FeatureNotSupportedYet(String featureName, int line) {
    }
}
