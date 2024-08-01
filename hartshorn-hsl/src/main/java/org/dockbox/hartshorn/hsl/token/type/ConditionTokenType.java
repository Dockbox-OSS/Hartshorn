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

import java.util.function.Consumer;

import org.dockbox.hartshorn.hsl.token.DefaultTokenCharacter;
import org.dockbox.hartshorn.hsl.token.TokenCharacter;
import org.dockbox.hartshorn.hsl.token.TokenMetaData;
import org.dockbox.hartshorn.hsl.token.TokenMetaDataBuilder;

/**
 * Represents a token type that is used to define conditions in the HSL language.
 *
 * @see TokenType
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public enum ConditionTokenType implements EnumTokenType {
    /**
     * '&gt;' character, representing a 'greater than' condition.
     */
    GREATER(DefaultTokenCharacter.GREATER),
    /**
     * '&lt;' character, representing a 'less than' condition.
     */
    LESS(DefaultTokenCharacter.LESS),

    /**
     * '==' character, representing an 'equal to' condition.
     */
    EQUAL_EQUAL(BaseTokenType.EQUAL),
    /**
     * '!=' character, representing a 'not equal to' condition.
     */
    BANG_EQUAL(BaseTokenType.BANG),

    /**
     * '&gt;=' character, representing a 'greater than or equal to' condition.
     */
    GREATER_EQUAL(GREATER),
    /**
     * '&lt;=' character, representing a 'less than or equal to' condition.
     */
    LESS_EQUAL(LESS),

    /**
     * '&amp;&amp;' character, representing a 'logical and' condition. Logical and conditions are
     * used to combine two or more conditions, and are only true if all conditions are true.
     */
    AND(builder -> builder.repeats(BitwiseTokenType.BITWISE_AND)),
    /**
     * '||' character, representing a 'logical or' condition. Logical or conditions are
     * used to combine two or more conditions, and are true if at least one condition is true.
     */
    OR(builder -> builder.repeats(BitwiseTokenType.BITWISE_OR)),

    /**
     * '?:' character, representing a shorthand ternary (elvis) condition. Ternary conditions are
     * used to evaluate a condition and return one of two values, based on the result of the condition.
     * The shorthand ternary condition is used to return a value if the condition is truthy, and a default
     * value if the condition is falsy. This is a shorthand for the full ternary condition, which is
     * not expressed in a single token, but rather an expression of multiple individual tokens.
     */
    ELVIS(builder -> builder.combines(BaseTokenType.QUESTION_MARK, BaseTokenType.COLON)),

    ;

    private final TokenMetaData metaData;

    ConditionTokenType(TokenCharacter character) {
        this(builder -> builder
                .representation(String.valueOf(character.character()))
                .characters(character)
        );
    }

    ConditionTokenType(TokenType combinesWith) {
        this(builder -> builder.combines(combinesWith, BaseTokenType.EQUAL));
    }

    ConditionTokenType(Consumer<TokenMetaDataBuilder> metaData) {
        TokenMetaDataBuilder builder = TokenMetaData.builder(this);
        metaData.accept(builder);
        this.metaData = builder.build();
    }

    @Override
    public TokenType delegate() {
        return metaData;
    }
}
