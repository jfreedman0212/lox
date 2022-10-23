package dev.freedman.jlox;

import java.util.Objects;

public sealed interface Token {
    public sealed interface Literal extends Token {
    }

    public sealed interface BinaryOperator<Operand, Result> extends Token {
        Result evaluateBinaryOperation(Operand left, Operand right);
    }

    public sealed interface UnaryOperator<Operand, Result> extends Token {
        Result evaluateUnaryOperation(Operand right);
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

    public record Minus(char lexeme, int line)
            implements BinaryOperator<Double, Double>, UnaryOperator<Double, Double> {
        @Override
        public Double evaluateBinaryOperation(Double left, Double right) {
            return left - right;
        }

        @Override
        public Double evaluateUnaryOperation(Double right) {
            return -right;
        }
    }

    public record Plus(char lexeme, int line) implements BinaryOperator<Object, Object> {
        @Override
        public Object evaluateBinaryOperation(Object left, Object right) {
            // handle valid cases (arithmetic addition and string concatenation)
            if (left instanceof Double leftDouble && right instanceof Double rightDouble) {
                return leftDouble + rightDouble;
            } else if (left instanceof String leftString && right instanceof String rightString) {
                return leftString + rightString;
            }
            // handle invalid cases
            if ((left instanceof Double && right instanceof String)
                    || (left instanceof String && right instanceof Double)) {
                throw new RuntimeException("cannot use + operator on a string and a number");
            }
            throw new RuntimeException(String.format("cannot use + operator between %s and %s", left, right));
        }
    }

    public record Semicolon(char lexeme, int line) implements Token {
    }

    public record Slash(char lexeme, int line) implements BinaryOperator<Double, Double> {
        @Override
        public Double evaluateBinaryOperation(Double left, Double right) {
            return left / right;
        }
    }

    public record Star(char lexeme, int line) implements BinaryOperator<Double, Double> {
        @Override
        public Double evaluateBinaryOperation(Double left, Double right) {
            return left * right;
        }
    }
    // endregion

    // region One or two character tokens
    public record Bang(char lexeme, int line) implements UnaryOperator<Boolean, Boolean> {
        @Override
        public Boolean evaluateUnaryOperation(Boolean right) {
            return !right;
        }
    }

    public record BangEqual(String lexeme, int line) implements BinaryOperator<Object, Boolean> {
        @Override
        public Boolean evaluateBinaryOperation(Object left, Object right) {
            return !Objects.equals(left, right);
        }
    }

    public record Equal(char lexeme, int line) implements Token {
    }

    public record EqualEqual(String lexeme, int line) implements BinaryOperator<Object, Boolean> {
        @Override
        public Boolean evaluateBinaryOperation(Object left, Object right) {
            return Objects.equals(left, right);
        }
    }

    public record Greater(char lexeme, int line) implements BinaryOperator<Double, Boolean> {
        @Override
        public Boolean evaluateBinaryOperation(Double left, Double right) {
            return left > right;
        }
    }

    public record GreaterEqual(String lexeme, int line) implements BinaryOperator<Double, Boolean> {
        @Override
        public Boolean evaluateBinaryOperation(Double left, Double right) {
            return left >= right;
        }
    }

    public record Less(char lexeme, int line) implements BinaryOperator<Double, Boolean> {
        @Override
        public Boolean evaluateBinaryOperation(Double left, Double right) {
            return left < right;
        }
    }

    public record LessEqual(String lexeme, int line) implements BinaryOperator<Double, Boolean> {
        @Override
        public Boolean evaluateBinaryOperation(Double left, Double right) {
            return left <= right;
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
