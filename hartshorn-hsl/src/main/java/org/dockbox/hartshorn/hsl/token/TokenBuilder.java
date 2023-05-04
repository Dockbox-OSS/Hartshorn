package org.dockbox.hartshorn.hsl.token;

public class TokenBuilder {

    private final TokenType type;
    private Object literal;
    private String lexeme;
    private int line;
    private int column;

    public TokenBuilder(final TokenType type) {
        this.type = type;
    }

    public TokenBuilder literal(final Object literal) {
        this.literal = literal;
        return this;
    }

    public TokenBuilder lexeme(final String lexeme) {
        this.lexeme = lexeme;
        return this;
    }

    public TokenBuilder line(final int line) {
        this.line = line;
        return this;
    }

    public TokenBuilder column(final int column) {
        this.column = column;
        return this;
    }

    public TokenBuilder position(final Token token) {
        return this
                .line(token.line())
                .column(token.column());
    }

    public Token build() {
        return new Token(this.type, this.lexeme, this.literal, this.line, this.column);
    }

    public TokenBuilder virtual() {
        return this
                .line(-1)
                .column(-1);
    }
}
