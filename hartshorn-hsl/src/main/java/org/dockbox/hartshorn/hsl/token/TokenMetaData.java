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

package org.dockbox.hartshorn.hsl.token;

import org.dockbox.hartshorn.hsl.token.type.DelegateTokenType;
import org.dockbox.hartshorn.hsl.token.type.TokenType;

/**
 * A set of metadata providing extra information about a single {@link TokenType}. Token
 * metadata is immutable, as it is part of the lexical definition of a token.
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public class TokenMetaData implements DelegateTokenType {

    private final TokenType type;
    private final TokenType metadata;

    TokenMetaData(TokenType type, TokenType metadata) {
        this.type = type;
        this.metadata = metadata;
    }

    /**
     * Get the associated {@link TokenType} which is represented by this {@link TokenMetaData}.
     * @return The associated {@link TokenType}.
     */
    public TokenType type() {
        return this.type;
    }

    @Override
    public String tokenName() {
        return this.type.tokenName();
    }

    @Override
    public TokenType delegate() {
        return this.metadata;
    }

    /**
     * Creates a new builder for the given {@link TokenType}.
     * @param type The {@link TokenType} to attach to.
     * @return A new {@link TokenMetaDataBuilder}.
     */
    public static TokenMetaDataBuilder builder(TokenType type) {
        return new TokenMetaDataBuilder(type);
    }
}
