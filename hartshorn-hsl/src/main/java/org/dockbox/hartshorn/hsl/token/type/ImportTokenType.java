package org.dockbox.hartshorn.hsl.token.type;

import org.dockbox.hartshorn.hsl.token.TokenMetaData;

public enum ImportTokenType implements EnumTokenType {
    IMPORT
    ;

    private final TokenMetaData metaData;

    ImportTokenType() {
        this.metaData = TokenMetaData.builder(this)
                .keyword(true)
                .standaloneStatement(true)
                .ok();
    }

    @Override
    public TokenType delegate() {
        return metaData;
    }
}
