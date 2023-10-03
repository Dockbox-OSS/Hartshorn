package org.dockbox.hartshorn.hsl.token.type;

import org.dockbox.hartshorn.hsl.token.DefaultTokenCharacter;
import org.dockbox.hartshorn.hsl.token.TokenCharacter;
import org.dockbox.hartshorn.hsl.token.TokenMetaData;

public enum BaseTokenType implements EnumTokenType {
    QUESTION_MARK(DefaultTokenCharacter.QUESTION_MARK),
    COLON(DefaultTokenCharacter.COLON),
    COMMA(DefaultTokenCharacter.COMMA),
    DOT(DefaultTokenCharacter.DOT),
    SEMICOLON(DefaultTokenCharacter.SEMICOLON),
    EQUAL(DefaultTokenCharacter.EQUAL),
    BANG(DefaultTokenCharacter.BANG),
    ;

    private final TokenMetaData metaData;

    BaseTokenType(TokenCharacter representation) {
        this.metaData = TokenMetaData.builder(this)
                .representation(String.valueOf(representation.character()))
                .ok();
    }

    @Override
    public TokenType delegate() {
        return metaData;
    }
}
