package org.dockbox.hartshorn.hsl.token.type;

import org.dockbox.hartshorn.hsl.token.TokenMetaData;
import org.dockbox.hartshorn.hsl.token.TokenMetaDataBuilder;

import java.util.function.Consumer;

public enum LoopTokenType implements EnumTokenType {
    REPEAT(true),
    DO(true),
    WHILE(true),
    FOR(true),
    IN(false),

    RANGE(builder -> builder.repeats(BaseTokenType.DOT).ok()),
    ;

    private final TokenMetaData metaData;

    LoopTokenType(boolean standalone) {
        this(builder -> builder
                .keyword(true)
                .standaloneStatement(standalone)
        );
    }

    LoopTokenType(Consumer<TokenMetaDataBuilder> metaData) {
        TokenMetaDataBuilder builder = TokenMetaData.builder(this);
        metaData.accept(builder);
        this.metaData = builder.ok();
    }

    @Override
    public TokenType delegate() {
        return metaData;
    }
}
