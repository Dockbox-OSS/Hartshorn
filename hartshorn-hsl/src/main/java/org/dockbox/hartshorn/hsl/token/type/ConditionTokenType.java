/*
 * Copyright 2019-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dockbox.hartshorn.hsl.token.type;

import org.dockbox.hartshorn.hsl.token.DefaultTokenCharacter;
import org.dockbox.hartshorn.hsl.token.TokenCharacter;
import org.dockbox.hartshorn.hsl.token.TokenMetaData;
import org.dockbox.hartshorn.hsl.token.TokenMetaDataBuilder;

import java.util.function.Consumer;

public enum ConditionTokenType implements EnumTokenType {
    XOR_EQUAL(BitwiseTokenType.XOR),
    BITWISE_AND_EQUAL(BitwiseTokenType.BITWISE_AND),
    BITWISE_OR_EQUAL(BitwiseTokenType.BITWISE_OR),
    COMPLEMENT_EQUAL(BitwiseTokenType.COMPLEMENT),
    SHIFT_LEFT_EQUAL(BitwiseTokenType.SHIFT_LEFT),
    SHIFT_RIGHT_EQUAL(BitwiseTokenType.SHIFT_RIGHT),

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

    ConditionTokenType(TokenType assignsWithToken) {
        this(builder -> builder
                .combines(assignsWithToken, BaseTokenType.EQUAL)
                .assignsWith(assignsWithToken)
        );
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
