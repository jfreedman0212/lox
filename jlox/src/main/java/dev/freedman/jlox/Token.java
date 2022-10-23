package dev.freedman.jlox;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

public sealed interface Token {
    public sealed interface Literal extends Token {
    }

    int line();

    public sealed interface BinaryOperator extends Token {
        Object evaluateBinaryOperation(Object left, Object right) throws InterpreterException;
    }

    public sealed interface UnaryOperator extends Token {
        Object evaluateUnaryOperation(Object right) throws InterpreterException;
    }

    // region Single-character tokens
    public record LeftParenthesis(char lexeme, int line) implements Token {
    }

    public record RightParenthesis(char lexeme, int line) implements Token {
    }

    public record LeftBrace(char lexeme, int line) implements Token {
    }

    public record RightBrace(char lexeme, int line) implements Token {
    }

    public record Comma(char lexeme, int line) implements Token {
    }

    public record Dot(char lexeme, int line) implements Token {
    }

    public record Minus(char lexeme, int line) implements BinaryOperator, UnaryOperator {
        @Override
        public Object evaluateBinaryOperation(Object left, Object right) throws InterpreterException {
            if (left instanceof Double leftDouble && right instanceof Double rightDouble) {
                return leftDouble - rightDouble;
            }
            throw new InterpreterException(
                    new InterpreterIssue.InvalidTypesForOperation(
                            "Minus operator (-)",
                            Collections.singletonList("numbers"),
                            Arrays.asList(Objects.toString(left), Objects.toString(right)),
                            line));
        }

        @Override
        public Object evaluateUnaryOperation(Object right) throws InterpreterException {
            if (right instanceof Double val) {
                return -val;
            }
            throw new InterpreterException(
                    new InterpreterIssue.InvalidTypesForOperation(
                            "Minus operator (-)",
                            Collections.singletonList("numbers"),
                            Collections.singletonList(Objects.toString(right)),
                            line));
        }
    }

    public record Plus(char lexeme, int line) implements BinaryOperator {
        @Override
        public Object evaluateBinaryOperation(Object left, Object right) throws InterpreterException {
            // handle valid cases (arithmetic addition and string concatenation)
            if (left instanceof Double leftDouble && right instanceof Double rightDouble) {
                return leftDouble + rightDouble;
            } else if (left instanceof String leftString && right instanceof String rightString) {
                return leftString + rightString;
            }
            // handle invalid cases
            if ((left instanceof Double && right instanceof String)
                    || (left instanceof String && right instanceof Double)) {
                throw new InterpreterException(
                        new InterpreterIssue.InvalidTypesForOperation(
                                "Plus operator (+)",
                                Arrays.asList("numbers", "strings"),
                                Arrays.asList(Objects.toString(left), Objects.toString(right)),
                                line));
            }
            throw new InterpreterException(
                    new InterpreterIssue.InvalidTypesForOperation(
                            "Plus operator (+)",
                            Arrays.asList(Objects.toString(left), Objects.toString(right)),
                            Arrays.asList(Objects.toString(left), Objects.toString(right)),
                            line));
        }
    }

    public record Semicolon(char lexeme, int line) implements Token {
    }

    public record Slash(char lexeme, int line) implements BinaryOperator {
        @Override
        public Object evaluateBinaryOperation(Object left, Object right) throws InterpreterException {
            if (left instanceof Double leftDouble && right instanceof Double rightDouble) {
                return leftDouble / rightDouble;
            }
            throw new InterpreterException(
                    new InterpreterIssue.InvalidTypesForOperation(
                            "Division operator (/)",
                            Collections.singletonList("numbers"),
                            Arrays.asList(Objects.toString(left), Objects.toString(right)),
                            line));
        }
    }

    public record Star(char lexeme, int line) implements BinaryOperator {
        @Override
        public Object evaluateBinaryOperation(Object left, Object right) throws InterpreterException {
            if (left instanceof Double leftDouble && right instanceof Double rightDouble) {
                return leftDouble * rightDouble;
            }
            throw new InterpreterException(
                    new InterpreterIssue.InvalidTypesForOperation(
                            "Multiplication operator (*)",
                            Collections.singletonList("numbers"),
                            Arrays.asList(Objects.toString(left), Objects.toString(right)),
                            line));
        }
    }
    // endregion

    // region One or two character tokens
    public record Bang(char lexeme, int line) implements UnaryOperator {
        @Override
        public Object evaluateUnaryOperation(Object right) throws InterpreterException {
            final boolean booleanToNegate;
            if (Objects.isNull(right)) {
                // nil gets coalesced to false (i.e. nil is "falsy")
                booleanToNegate = false;
            } else if (right instanceof Boolean bool) {
                // booleans are treated as they are
                booleanToNegate = bool;
            } else {
                // all other values coalesce to true (i.e. all other values are "truthy")
                booleanToNegate = true;
            }
            return !booleanToNegate;
        }
    }

    public record BangEqual(String lexeme, int line) implements BinaryOperator {
        @Override
        public Boolean evaluateBinaryOperation(Object left, Object right) throws InterpreterException {
            return !Objects.equals(left, right);
        }
    }

    public record Equal(char lexeme, int line) implements Token {
    }

    public record EqualEqual(String lexeme, int line) implements BinaryOperator {
        @Override
        public Boolean evaluateBinaryOperation(Object left, Object right) {
            return Objects.equals(left, right);
        }
    }

    public record Greater(char lexeme, int line) implements BinaryOperator {
        @Override
        public Object evaluateBinaryOperation(Object left, Object right) throws InterpreterException {
            if (left instanceof Double leftDouble && right instanceof Double rightDouble) {
                return leftDouble > rightDouble;
            }
            throw new InterpreterException(
                    new InterpreterIssue.InvalidTypesForOperation(
                            "Greater Than operator (>)",
                            Collections.singletonList("numbers"),
                            Arrays.asList(Objects.toString(left), Objects.toString(right)),
                            line));
        }
    }

    public record GreaterEqual(String lexeme, int line) implements BinaryOperator {
        @Override
        public Object evaluateBinaryOperation(Object left, Object right) throws InterpreterException {
            if (left instanceof Double leftDouble && right instanceof Double rightDouble) {
                return leftDouble >= rightDouble;
            }
            throw new InterpreterException(
                    new InterpreterIssue.InvalidTypesForOperation(
                            "Greater Than Or Equal To operator (>=)",
                            Collections.singletonList("numbers"),
                            Arrays.asList(Objects.toString(left), Objects.toString(right)),
                            line));
        }
    }

    public record Less(char lexeme, int line) implements BinaryOperator {
        @Override
        public Object evaluateBinaryOperation(Object left, Object right) throws InterpreterException {
            if (left instanceof Double leftDouble && right instanceof Double rightDouble) {
                return leftDouble < rightDouble;
            }
            throw new InterpreterException(
                    new InterpreterIssue.InvalidTypesForOperation(
                            "Less Than operator (<)",
                            Collections.singletonList("numbers"),
                            Arrays.asList(Objects.toString(left), Objects.toString(right)),
                            line));
        }
    }

    public record LessEqual(String lexeme, int line) implements BinaryOperator {
        @Override
        public Object evaluateBinaryOperation(Object left, Object right) throws InterpreterException {
            if (left instanceof Double leftDouble && right instanceof Double rightDouble) {
                return leftDouble <= rightDouble;
            }
            throw new InterpreterException(
                    new InterpreterIssue.InvalidTypesForOperation(
                            "Less Than Or Equal To operator (<=)",
                            Collections.singletonList("numbers"),
                            Arrays.asList(Objects.toString(left), Objects.toString(right)),
                            line));
        }
    }
    // endregion

    // region Literals
    public record Identifier(String lexeme, int line) implements Literal {
    }

    public record StringLiteral(String lexeme, int line, String value) implements Literal {
    }

    public record Number(String lexeme, int line, double value) implements Literal {
    }
    // endregion

    // region Keywords
    public record And(String lexeme, int line) implements Token {
    }

    public record Class(String lexeme, int line) implements Token {
    }

    public record Else(String lexeme, int line) implements Token {
    }

    public record False(String lexeme, int line) implements Literal {
    }

    public record Fun(String lexeme, int line) implements Token {
    }

    public record For(String lexeme, int line) implements Token {
    }

    public record If(String lexeme, int line) implements Token {
    }

    public record Nil(String lexeme, int line) implements Literal {
    }

    public record Or(String lexeme, int line) implements Token {
    }

    public record Print(String lexeme, int line) implements Token {
    }

    public record Return(String lexeme, int line) implements Token {
    }

    public record Super(String lexeme, int line) implements Token {
    }

    public record This(String lexeme, int line) implements Token {
    }

    public record True(String lexeme, int line) implements Literal {
    }

    public record Var(String lexeme, int line) implements Token {
    }

    public record While(String lexeme, int line) implements Token {
    }
    // endregion

    public record EndOfFile(int line) implements Token {
    }
}
