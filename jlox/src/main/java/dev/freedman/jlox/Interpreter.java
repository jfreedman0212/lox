package dev.freedman.jlox;

public class Interpreter {
    public static Object interpret(final Expr expr) {
        if (expr instanceof Expr.Unary unaryExpr) {
            final Object right = interpret(unaryExpr.right());
            return unaryExpr.operator().evaluateUnaryOperation(right);
        } else if (expr instanceof Expr.Grouping groupingExpr) {
            return interpret(groupingExpr.expression());
        } else if (expr instanceof Expr.Literal literalExpr) {
            final Token.Literal literal = literalExpr.value();
            if (literal instanceof Token.Identifier) {
                throw new RuntimeException("Identifiers not yet!");
            } else if (literal instanceof Token.Nil) {
                return null;
            } else if (literal instanceof Token.Number number) {
                return Double.valueOf(number.value());
            } else if (literal instanceof Token.StringLiteral string) {
                return string.value();
            } else if (literal instanceof Token.True) {
                return true;
            } else if (literal instanceof Token.False) {
                return false;
            }
        } else if (expr instanceof Expr.Binary binaryExpr) {
            final Object left = interpret(binaryExpr.left());
            final Object right = interpret(binaryExpr.right());
            final Token.BinaryOperator operator = binaryExpr.operator();
            return operator.evaluateBinaryOperation(left, right);
        }
        return null;
    }
}
