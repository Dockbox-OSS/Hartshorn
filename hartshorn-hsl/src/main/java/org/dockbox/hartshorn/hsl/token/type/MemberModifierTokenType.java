package org.dockbox.hartshorn.hsl.token.type;

import org.dockbox.hartshorn.hsl.token.TokenMetaData;

public enum MemberModifierTokenType implements EnumTokenType {
    PUBLIC,
    PRIVATE,
    STATIC,
    ABSTRACT,
    FINAL,
    ;

    private final TokenMetaData metaData;

    MemberModifierTokenType() {
        this.metaData = TokenMetaData.builder(this)
                .keyword(true)
                .ok();
    }

    @Override
    public TokenType delegate() {
        return metaData;
    }
}
