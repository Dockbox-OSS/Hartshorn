package org.dockbox.hartshorn.hsl.token.type;

import org.dockbox.hartshorn.hsl.token.TokenMetaData;

public enum TypeTokenType implements EnumTokenType {
    AS,
    IS,
    NEW,
    TYPEOF,
    INSTANCEOF,
    ;

    private final TokenMetaData metaData;

    TypeTokenType() {
        this.metaData = TokenMetaData.builder(this)
                .keyword(true)
                .ok();
    }

    @Override
    public TokenType delegate() {
        return metaData;
    }
}
