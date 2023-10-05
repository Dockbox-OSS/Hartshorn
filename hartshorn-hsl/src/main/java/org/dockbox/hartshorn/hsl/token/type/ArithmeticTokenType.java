package org.dockbox.hartshorn.hsl.token.type;

import org.dockbox.hartshorn.hsl.token.DefaultTokenCharacter;
import org.dockbox.hartshorn.hsl.token.TokenMetaData;
import org.dockbox.hartshorn.hsl.token.TokenMetaDataBuilder;
import org.dockbox.hartshorn.util.Customizer;

public enum ArithmeticTokenType implements EnumTokenType {
    PLUS(DefaultTokenCharacter.PLUS),
    MINUS(DefaultTokenCharacter.MINUS),
    STAR(DefaultTokenCharacter.STAR),
    SLASH(DefaultTokenCharacter.SLASH),
    MODULO(DefaultTokenCharacter.MODULO),

    PLUS_PLUS(builder -> builder.repeats(PLUS)),
    MINUS_MINUS(builder -> builder.repeats(MINUS)),
    ;

    private final TokenMetaData metaData;

    ArithmeticTokenType(DefaultTokenCharacter character) {
        this(builder -> builder
                .representation(String.valueOf(character.character()))
                .characters(character)
        );
    }

    ArithmeticTokenType(Customizer<TokenMetaDataBuilder> customizer) {
        TokenMetaDataBuilder builder = TokenMetaData.builder(this);
        customizer.configure(builder);
        this.metaData = builder.ok();
    }

    @Override
    public TokenType delegate() {
        return metaData;
    }
}
