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

import org.dockbox.hartshorn.hsl.token.TokenMetaData;
import org.dockbox.hartshorn.hsl.token.TokenMetaDataBuilder;

/**
 * Represents the different types of loop tokens that can be used in the HSL language. Loop tokens are used to define
 * blocks of code that are executed repeatedly, based on a condition or a range of values.
 *
 * @see TokenType
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public enum LoopTokenType implements EnumTokenType {
    /**
     * 'repeat' token, representing a loop that is executed a fixed number of times.
     */
    REPEAT(true),
    /**
     * 'do' token, representing a loop that is executed at least once, and then repeatedly as long as a condition is
     * met.
     */
    DO(true),
    /**
     * 'while' token, representing a loop that is executed repeatedly as long as a condition is met.
     */
    WHILE(true),
    /**
     * 'for' token, representing a loop that is executed repeatedly either for a range of values (for-each) or for a
     * condition (for-i).
     */
    FOR(true),
    /**
     * 'in' token, representing a loop that is executed repeatedly for a range of values.
     */
    IN(false),
    /**
     * 'range' token, representing a loop that is executed repeatedly for a range of values from a start to an end.
     */
    RANGE(builder -> builder.repeats(BaseTokenType.DOT).build()),
    ;

    private final TokenMetaData metaData;

    LoopTokenType(boolean standalone) {
        this(builder -> builder
                .keyword(true)
                .standaloneStatement(standalone)
        );
    }

    LoopTokenType(Consumer<TokenMetaDataBuilder> metaData) {
        TokenMetaDataBuilder builder = TokenMetaData.builder(this);
        metaData.accept(builder);
        this.metaData = builder.build();
    }

    @Override
    public TokenType delegate() {
        return metaData;
    }
}
