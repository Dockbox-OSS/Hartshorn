package org.dockbox.hartshorn.hsl.token.type;

import org.dockbox.hartshorn.hsl.token.TokenMetaData;
import org.dockbox.hartshorn.hsl.token.TokenMetaDataBuilder;
import org.dockbox.hartshorn.util.Customizer;

public enum AssertTokenType implements EnumTokenType {
    ASSERT(builder -> builder.keyword(true).standaloneStatement(true).reserved(true)),
    TEST(builder -> builder.keyword(true).standaloneStatement(true).reserved(true)),
    ;

    private final TokenMetaData metaData;

    AssertTokenType(Customizer<TokenMetaDataBuilder> customizer) {
        TokenMetaDataBuilder builder = TokenMetaData.builder(this);
        customizer.configure(builder);
        this.metaData = builder.ok();
    }

    @Override
    public TokenType delegate() {
        return metaData;
    }
}
