package dev.freedman.jlox;

import java.util.List;

public class LoxFunction implements LoxCallable {
    private final Statement.Function functionDeclaration;

    public LoxFunction(final Statement.Function functionDeclaration) {
        this.functionDeclaration = functionDeclaration;
    }

    @Override
    public int arity() {
        return functionDeclaration.parameters().size();
    }

    @Override
    public Object call(final Interpreter interpreter, final List<Object> arguments) throws InterpreterException {
        final Environment environment = new Environment(interpreter.globals);
        for (int i = 0; i < functionDeclaration.parameters().size(); ++i) {
            final Token.Identifier parameter = functionDeclaration.parameters().get(i);
            final Object argument = arguments.get(i);
            environment.declare(parameter, argument);
        }
        interpreter.executeBlock(functionDeclaration.body(), environment);
        return null;
    }

    @Override
    public String toString() {
        return String.format("<fun %s>", functionDeclaration.name().lexeme());
    }

}
