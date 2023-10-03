package org.dockbox.hartshorn.hsl.token.type;

import org.dockbox.hartshorn.hsl.token.TokenMetaData;

public enum BitwiseAssignmentTokenType implements EnumTokenType {
    XOR_EQUAL(BitwiseTokenType.XOR),
    BITWISE_AND_EQUAL(BitwiseTokenType.BITWISE_AND),
    BITWISE_OR_EQUAL(BitwiseTokenType.BITWISE_OR),
    COMPLEMENT_EQUAL(BitwiseTokenType.COMPLEMENT),
    SHIFT_LEFT_EQUAL(BitwiseTokenType.SHIFT_LEFT),
    SHIFT_RIGHT_EQUAL(BitwiseTokenType.SHIFT_RIGHT),
    ;

    private final TokenMetaData metaData;

    BitwiseAssignmentTokenType(TokenType assignsWithToken) {
        this.metaData = TokenMetaData.builder(this)
                .combines(assignsWithToken, BaseTokenType.EQUAL)
                .assignsWith(assignsWithToken)
                .ok();
    }
    @Override
    public TokenType delegate() {
        return this.metaData;
    }
}
