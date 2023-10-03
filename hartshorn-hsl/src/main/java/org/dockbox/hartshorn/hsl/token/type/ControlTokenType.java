package org.dockbox.hartshorn.hsl.token.type;

import org.dockbox.hartshorn.hsl.token.TokenMetaData;
import org.dockbox.hartshorn.hsl.token.TokenMetaDataBuilder;

import java.util.function.Consumer;

public enum ControlTokenType implements EnumTokenType {
    IF(true),
    ELSE(false),
    SWITCH(true),
    CASE(false),
    BREAK(true),
    CONTINUE(true),
    RETURN(true),
    YIELD(true),
    DEFAULT(false),

    ARROW(builder -> builder.combines(ArithmeticTokenType.MINUS, ConditionTokenType.GREATER).ok()),
    ;

    private final TokenMetaData metaData;

    ControlTokenType(boolean standalone) {
        this(builder -> builder
                .standaloneStatement(standalone)
                .keyword(true)
        );
    }

    ControlTokenType(Consumer<TokenMetaDataBuilder> metaData) {
        TokenMetaDataBuilder builder = TokenMetaData.builder(this);
        metaData.accept(builder);
        this.metaData = builder.ok();
    }

    @Override
    public TokenType delegate() {
        return metaData;
    }
}
