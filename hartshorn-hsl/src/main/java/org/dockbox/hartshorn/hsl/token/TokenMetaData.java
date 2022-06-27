package org.dockbox.hartshorn.hsl.token;

public class TokenMetaData {

    private final TokenType type;
    private final String representation;
    private final boolean keyword;
    private final boolean standaloneStatement;

    TokenMetaData(final TokenMetaDataBuilder builder) {
        this.type = builder.type;
        this.representation = builder.representation;
        this.keyword = builder.keyword;
        this.standaloneStatement = builder.standaloneStatement;
    }

    public TokenType type() {
        return this.type;
    }

    public String representation() {
        return this.representation;
    }

    public boolean keyword() {
        return this.keyword;
    }

    public boolean standaloneStatement() {
        return this.standaloneStatement;
    }

    public static TokenMetaDataBuilder builder(final TokenType type) {
        return new TokenMetaDataBuilder(type);
    }
}
