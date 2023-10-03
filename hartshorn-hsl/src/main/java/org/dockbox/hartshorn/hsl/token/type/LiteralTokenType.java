package org.dockbox.hartshorn.hsl.token.type;

import org.dockbox.hartshorn.hsl.token.TokenMetaData;

public enum LiteralTokenType implements EnumTokenType {
    IDENTIFIER,
    STRING,
    NUMBER,
    CHAR,
    EOF,
    NULL("null"),
    TRUE("true"),
    FALSE("false"),
    ;

    private final TokenMetaData metaData;

    LiteralTokenType(String defaultLexeme) {
        this.metaData = TokenMetaData.builder(this).defaultLexeme(defaultLexeme).ok();
    }

    LiteralTokenType() {
        this.metaData = TokenMetaData.builder(this).ok();
    }

    @Override
    public TokenType delegate() {
        return this.metaData;
    }
}
