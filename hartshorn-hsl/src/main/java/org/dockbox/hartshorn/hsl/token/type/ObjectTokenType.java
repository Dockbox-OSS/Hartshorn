package org.dockbox.hartshorn.hsl.token.type;

import org.dockbox.hartshorn.hsl.token.TokenMetaData;

public enum ObjectTokenType implements EnumTokenType {
    SUPER,
    THIS,
    ;

    private final TokenMetaData metaData;

    ObjectTokenType() {
        this.metaData = TokenMetaData.builder(this)
                .keyword(true)
                .ok();
    }

    @Override
    public TokenType delegate() {
        return metaData;
    }
}
