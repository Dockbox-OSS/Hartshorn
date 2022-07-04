/*
 * Copyright 2019-2022 the original author or authors.
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

/**
 * A set of metadata providing extra information about a single {@link TokenType}. Token
 * metadata is immutable, as it is part of the lexical definition of a token.
 *
 * @author Guus Lieben
 * @since 22.4
 */
public class TokenMetaData {

    private final TokenType type;
    private final String representation;
    private final boolean keyword;
    private final boolean standaloneStatement;

    TokenMetaData(final TokenMetaDataBuilder builder) {
        this.type = builder.type;
        this.representation = builder.representation;
        this.keyword = builder.keyword;
        this.standaloneStatement = builder.standaloneStatement;
    }

    /**
     * Get the associated {@link TokenType} which is represented by this {@link TokenMetaData}.
     * @return The associated {@link TokenType}.
     */
    public TokenType type() {
        return this.type;
    }

    /**
     * Gets the standard representation of the {@link TokenType}.
     * @return The representation of the token.
     */
    public String representation() {
        return this.representation;
    }

    /**
     * Gets whether the {@link TokenType} represents a keyword.
     * @return {@code true} if the token represents a keyword, or {@code false}.
     */
    public boolean keyword() {
        return this.keyword;
    }

    /**
     * Gets whether the {@link TokenType} can be used as a standalone statement. This
     * is typically used to indicate a token is not part of an expression statement,
     * but can optionally accept expressions when parsed.
     * @return {@code true} if the token can be used as a standalone statement.
     */
    public boolean standaloneStatement() {
        return this.standaloneStatement;
    }

    /**
     * Creates a new builder for the given {@link TokenType}.
     * @param type The {@link TokenType} to attach to.
     * @return A new {@link TokenMetaDataBuilder}.
     */
    public static TokenMetaDataBuilder builder(final TokenType type) {
        return new TokenMetaDataBuilder(type);
    }
}
