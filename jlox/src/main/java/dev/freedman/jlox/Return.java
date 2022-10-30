package dev.freedman.jlox;

public class Return extends RuntimeException {
    private final Object value;
    private final Token.Return token;

    public Return(final Object value, final Token.Return token) {
        super(null, null, false, false);
        this.value = value;
        this.token = token;
    }

    public Object getValue() {
        return this.value;
    }

    public Token.Return getToken() {
        return this.token;
    }
}
