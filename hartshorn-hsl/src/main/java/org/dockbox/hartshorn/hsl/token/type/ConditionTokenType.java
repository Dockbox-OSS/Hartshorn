package org.dockbox.hartshorn.hsl.token.type;

import org.dockbox.hartshorn.hsl.token.DefaultTokenCharacter;
import org.dockbox.hartshorn.hsl.token.TokenCharacter;
import org.dockbox.hartshorn.hsl.token.TokenMetaData;
import org.dockbox.hartshorn.hsl.token.TokenMetaDataBuilder;

import java.util.function.Consumer;

public enum ConditionTokenType implements EnumTokenType {
    GREATER(DefaultTokenCharacter.GREATER),
    LESS(DefaultTokenCharacter.LESS),

    EQUAL_EQUAL(BaseTokenType.EQUAL),
    BANG_EQUAL(BaseTokenType.BANG),
    GREATER_EQUAL(GREATER),
    LESS_EQUAL(LESS),

    AND(builder -> builder.repeats(BitwiseTokenType.BITWISE_AND)),
    OR(builder -> builder.repeats(BitwiseTokenType.BITWISE_OR)),

    ELVIS(builder -> builder.combines(BaseTokenType.QUESTION_MARK, BaseTokenType.COLON)),

    ;

    private final TokenMetaData metaData;

    ConditionTokenType(TokenCharacter character) {
        this(builder -> builder.representation(String.valueOf(character.character())));
    }

    ConditionTokenType(TokenType combinesWith) {
        this(builder -> builder.combines(combinesWith, BaseTokenType.EQUAL));
    }

    ConditionTokenType(Consumer<TokenMetaDataBuilder> metaData) {
        TokenMetaDataBuilder builder = TokenMetaData.builder(this);
        metaData.accept(builder);
        this.metaData = builder.ok();
    }

    @Override
    public TokenType delegate() {
        return metaData;
    }
}
