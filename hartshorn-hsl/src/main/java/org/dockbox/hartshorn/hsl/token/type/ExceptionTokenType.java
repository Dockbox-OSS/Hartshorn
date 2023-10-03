package org.dockbox.hartshorn.hsl.token.type;

import org.dockbox.hartshorn.hsl.token.TokenMetaData;
import org.dockbox.hartshorn.hsl.token.TokenMetaDataBuilder;

public enum ExceptionTokenType implements EnumTokenType {
    THROW(true),
    TRY(true),
    CATCH(false),
    FINALLY(false),
    ;

    private final TokenMetaData metaData;

    ExceptionTokenType(boolean standalone) {
        TokenMetaDataBuilder builder = TokenMetaData.builder(this)
                .reserved(true)
                .standaloneStatement(standalone)
                .keyword(true);
        this.metaData = builder.ok();
    }

    @Override
    public TokenType delegate() {
        return metaData;
    }
}
