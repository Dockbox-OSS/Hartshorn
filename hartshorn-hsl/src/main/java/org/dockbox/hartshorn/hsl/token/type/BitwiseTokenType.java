package org.dockbox.hartshorn.hsl.token.type;

import org.dockbox.hartshorn.hsl.token.DefaultTokenCharacter;
import org.dockbox.hartshorn.hsl.token.TokenMetaData;
import org.dockbox.hartshorn.hsl.token.TokenMetaDataBuilder;
import org.dockbox.hartshorn.util.Customizer;

public enum BitwiseTokenType implements EnumTokenType {
    XOR(DefaultTokenCharacter.CARET),
    BITWISE_AND(DefaultTokenCharacter.AMPERSAND),
    BITWISE_OR(DefaultTokenCharacter.PIPE),
    COMPLEMENT(DefaultTokenCharacter.TILDE),

    SHIFT_RIGHT(builder -> builder.repeats(DefaultTokenCharacter.GREATER)),
    SHIFT_LEFT(builder -> builder.repeats(DefaultTokenCharacter.LESS)),
    LOGICAL_SHIFT_RIGHT(builder -> builder.combines(DefaultTokenCharacter.GREATER, DefaultTokenCharacter.GREATER, DefaultTokenCharacter.GREATER)),

    ;

    private final TokenMetaData metaData;

    BitwiseTokenType(DefaultTokenCharacter character) {
        this(builder -> builder.representation(String.valueOf(character.character())));
    }

    BitwiseTokenType(Customizer<TokenMetaDataBuilder> customizer) {
        TokenMetaDataBuilder builder = TokenMetaData.builder(this);
        customizer.configure(builder);
        this.metaData = builder.ok();
    }

    @Override
    public TokenType delegate() {
        return metaData;
    }
}
