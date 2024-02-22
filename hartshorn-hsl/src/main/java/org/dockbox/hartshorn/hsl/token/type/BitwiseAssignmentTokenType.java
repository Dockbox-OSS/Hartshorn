/*
 * Copyright 2019-2024 the original author or authors.
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

import org.dockbox.hartshorn.hsl.token.TokenMetaData;

/**
 * Represents a token type that combines a bitwise operator with an assignment operator. This
 * allows for the creation of a single token type that allows users to express a bitwise operation
 * and an assignment in a single token. This is purely a convenience feature, as the same effect
 * can be achieved by using the bitwise operator and the assignment operator separately.
 *
 * @see BitwiseTokenType
 * @see BaseTokenType#EQUAL
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public enum BitwiseAssignmentTokenType implements EnumTokenType {
    /**
     * Represents the bitwise XOR assignment operator.
     * @see BitwiseTokenType#XOR
     */
    XOR_EQUAL(BitwiseTokenType.XOR),
    /**
     * Represents the bitwise AND assignment operator.
     * @see BitwiseTokenType#BITWISE_AND
     */
    BITWISE_AND_EQUAL(BitwiseTokenType.BITWISE_AND),
    /**
     * Represents the bitwise OR assignment operator.
     * @see BitwiseTokenType#BITWISE_OR
     */
    BITWISE_OR_EQUAL(BitwiseTokenType.BITWISE_OR),
    /**
     * Represents the bitwise complement assignment operator.
     * @see BitwiseTokenType#COMPLEMENT
     */
    COMPLEMENT_EQUAL(BitwiseTokenType.COMPLEMENT),
    /**
     * Represents the bitwise shift right assignment operator.
     * @see BitwiseTokenType#SHIFT_RIGHT
     */
    SHIFT_LEFT_EQUAL(BitwiseTokenType.SHIFT_LEFT),
    /**
     * Represents the bitwise shift left assignment operator.
     * @see BitwiseTokenType#SHIFT_LEFT
     */
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
