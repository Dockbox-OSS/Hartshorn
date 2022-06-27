package org.dockbox.hartshorn.hsl.token;

import java.util.Locale;

public class TokenMetaDataBuilder {

    final TokenType type;
    String representation;
    boolean keyword;
    boolean standaloneStatement;

    TokenMetaDataBuilder(final TokenType type) {
        this.type = type;
        this.representation = type.name().toLowerCase(Locale.ROOT);
    }

    public TokenMetaDataBuilder representation(final String representation) {
        this.representation = representation;
        return this;
    }

    public TokenMetaDataBuilder combines(final TokenType... types) {
        final StringBuilder combined = new StringBuilder();
        for (final TokenType type : types) {
            combined.append(type.representation());
        }
        this.representation = combined.toString();
        return this;
    }

    public TokenMetaDataBuilder repeats(final TokenType type) {
        return this.combines(type, type);
    }

    public TokenMetaDataBuilder keyword(final boolean keyword) {
        this.keyword = keyword;
        return this;
    }

    public TokenMetaDataBuilder standaloneStatement(final boolean standaloneStatement) {
        this.standaloneStatement = standaloneStatement;
        return this;
    }

    public TokenMetaData ok() {
        return new TokenMetaData(this);
    }
}
