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

import org.dockbox.hartshorn.hsl.token.DefaultTokenCharacter;
import org.dockbox.hartshorn.hsl.token.TokenMetaData;
import org.dockbox.hartshorn.hsl.token.TokenMetaDataBuilder;
import org.dockbox.hartshorn.util.Customizer;

/**
 * Represents the different bitwise operators that are available in the HSL language.
 *
 * @see TokenType
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public enum BitwiseTokenType implements EnumTokenType {
    /**
     * The bitwise XOR operator (^). XOR is a binary operator that returns true if exactly one
     * of the two bits is 1. If both bits are 0 or both bits are 1, the result is false.
     *
     * @see <a href="https://mathworld.wolfram.com/XOR.html">Worlfram MathWorld: XOR</a>
     */
    XOR(DefaultTokenCharacter.CARET),
    /**
     * The bitwise AND operator (&). AND is a binary operator that returns true if both bits are 1.
     *
     * @see <a href="https://mathworld.wolfram.com/AND.html">Worlfram MathWorld: AND</a>
     */
    BITWISE_AND(DefaultTokenCharacter.AMPERSAND),
    /**
     * The bitwise OR operator (|). OR is a binary operator that returns true if at least one of the
     * two bits is 1.
     *
     * @see <a href="https://mathworld.wolfram.com/OR.html">Worlfram MathWorld: OR</a>
     */
    BITWISE_OR(DefaultTokenCharacter.PIPE),
    /**
     * The bitwise NOT operator (~). NOT is a unary operator that returns the opposite of the input bit.
     *
     * @see <a href="https://mathworld.wolfram.com/NOT.html">Worlfram MathWorld: NOT</a>
     */
    COMPLEMENT(DefaultTokenCharacter.TILDE),

    /**
     * The bitwise shift right operator (>>). This operator shifts the bits of the first operand to the right
     * by the number of positions specified by the second operand.
     *
     * <p>For example, 8 >> 1 would result in 4, as the bits of 8 (1000) are shifted to the right by 1
     * position, resulting in 100.
     */
    SHIFT_RIGHT(builder -> builder.repeats(DefaultTokenCharacter.GREATER)),

    /**
     * The bitwise shift left operator (<<). This operator shifts the bits of the first operand to the left
     * by the number of positions specified by the second operand.
     *
     * <p>For example, 8 << 1 would result in 16, as the bits of 8 (1000) are shifted to the left by 1
     * position, resulting in 10000.
     */
    SHIFT_LEFT(builder -> builder.repeats(DefaultTokenCharacter.LESS)),

    /**
     * The logical shift right operator (>>>). This operator shifts the bits of the first operand to the right
     * by the number of positions specified by the second operand, and fills the leftmost bits with 0s.
     *
     * <p>For example, 1011 >>> 1 would result in 0101, as the least significant bit (1) is shifted to the
     * right by 1 position, and the leftmost bit is filled with a 0.
     */
    LOGICAL_SHIFT_RIGHT(builder -> builder.combines(DefaultTokenCharacter.GREATER, DefaultTokenCharacter.GREATER, DefaultTokenCharacter.GREATER)),

    ;

    private final TokenMetaData metaData;

    BitwiseTokenType(DefaultTokenCharacter character) {
        this(builder -> builder
                .representation(String.valueOf(character.character()))
                .characters(character)
        );
    }

    BitwiseTokenType(Customizer<TokenMetaDataBuilder> customizer) {
        TokenMetaDataBuilder builder = TokenMetaData.builder(this);
        customizer.configure(builder);
        this.metaData = builder.build();
    }

    @Override
    public TokenType delegate() {
        return metaData;
    }
}
