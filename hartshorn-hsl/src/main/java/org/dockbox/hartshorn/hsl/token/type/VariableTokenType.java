package org.dockbox.hartshorn.hsl.token.type;

import org.dockbox.hartshorn.hsl.token.TokenMetaData;

public enum VariableTokenType implements EnumTokenType {
    VAR,
    ;

    private final TokenMetaData metaData;

    VariableTokenType() {
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
