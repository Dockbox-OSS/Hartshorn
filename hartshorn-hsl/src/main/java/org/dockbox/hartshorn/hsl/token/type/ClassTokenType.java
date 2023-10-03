package org.dockbox.hartshorn.hsl.token.type;

import java.util.function.Consumer;

import org.dockbox.hartshorn.hsl.token.TokenMetaData;
import org.dockbox.hartshorn.hsl.token.TokenMetaDataBuilder;

public enum ClassTokenType implements EnumTokenType {
    CLASS,
    EXTENDS,
    INTERFACE,
    IMPLEMENTS,
    ENUM,
    ;

    private final TokenMetaData metaData;

    ClassTokenType() {
        this(builder -> builder.keyword(true));
    }

    ClassTokenType(Consumer<TokenMetaDataBuilder> metaData) {
        TokenMetaDataBuilder builder = TokenMetaData.builder(this);
        metaData.accept(builder);
        this.metaData = builder.ok();
    }

    @Override
    public TokenType delegate() {
        return metaData;
    }
}
