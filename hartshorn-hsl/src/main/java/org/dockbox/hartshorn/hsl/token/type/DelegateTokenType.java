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

import org.dockbox.hartshorn.hsl.token.TokenCharacter;

/**
 * Represents a token type that delegates to another token type. This is useful for creating
 * token types that are based on other token types, but have additional behavior or metadata.
 *
 * @see TokenType
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public interface DelegateTokenType extends TokenType {

    /**
     * Returns the token type that is being delegated to.
     *
     * @return the token type that is being delegated to
     */
    TokenType delegate();

    @Override
    default String representation() {
        return this.delegate().representation();
    }

    @Override
    default boolean keyword() {
        return this.delegate().keyword();
    }

    @Override
    default boolean standaloneStatement() {
        return this.delegate().standaloneStatement();
    }

    @Override
    default boolean reserved() {
        return this.delegate().reserved();
    }

    @Override
    default TokenType assignsWith() {
        return this.delegate().assignsWith();
    }

    @Override
    default String defaultLexeme() {
        return this.delegate().defaultLexeme();
    }

    @Override
    default TokenCharacter[] characters() {
        return this.delegate().characters();
    }
}
