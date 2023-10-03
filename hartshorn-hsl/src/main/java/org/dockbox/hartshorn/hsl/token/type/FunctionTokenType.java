package org.dockbox.hartshorn.hsl.token.type;

import java.util.function.Consumer;

import org.dockbox.hartshorn.hsl.token.TokenMetaData;
import org.dockbox.hartshorn.hsl.token.TokenMetaDataBuilder;

public enum FunctionTokenType implements EnumTokenType {
    FUNCTION(false),
    CONSTRUCTOR(builder -> builder.keyword(true).standaloneStatement(false).defaultLexeme("<init>")),

    PREFIX(false),
    INFIX(false),
    NATIVE(false),
    OPERATOR(false),
    OVERRIDE(false),
    ;

    private final TokenMetaData metaData;

    FunctionTokenType(boolean standalone) {
        this(builder -> builder.keyword(true).standaloneStatement(standalone));
    }

    FunctionTokenType(Consumer<TokenMetaDataBuilder> metaData) {
        TokenMetaDataBuilder builder = TokenMetaData.builder(this);
        metaData.accept(builder);
        this.metaData = builder.ok();
    }

    @Override
    public TokenType delegate() {
        return metaData;
    }
}
