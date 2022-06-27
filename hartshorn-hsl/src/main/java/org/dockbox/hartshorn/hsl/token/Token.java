package org.dockbox.hartshorn.hsl.token;

public class Token {

    private final TokenType type;
    private final Object literal;
    private final int line;
    private String lexeme;

    public Token(final TokenType type, final String lexeme, final int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = null;
        this.line = line;
    }

    public Token(final TokenType type, final String lexeme, final Object literal, final int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
    }

    public void concat(final Token token) {
        if(token == null) {
            return;
        }
        this.lexeme += token.lexeme;
    }

    public String lexeme() {
        return this.lexeme;
    }

    public Object literal() {
        return this.literal;
    }

    public TokenType type() {
        return this.type;
    }

    public int line() {
        return this.line;
    }

    public String toString() {
        return "Token[%s @ line %d = %s / %s]".formatted(this.type, this.line, this.lexeme, this.literal);
    }
}
