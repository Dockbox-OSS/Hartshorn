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

package org.dockbox.hartshorn.hsl.token;

import org.dockbox.hartshorn.hsl.token.type.TokenType;

/**
 * A set of metadata providing extra information about a single {@link TokenType}. Token
 * metadata is immutable, as it is part of the lexical definition of a token.
 *
 * @author Guus Lieben
 * @since 0.4.12
 */
public class TokenMetaData implements TokenType {

    private final TokenType type;
    private final String representation;
    private final boolean keyword;
    private final boolean standaloneStatement;
    private final boolean reserved;
    private final TokenType assignsWith;
    private final String defaultLexeme;
    private final TokenCharacter[] characters;

    TokenMetaData(TokenType type, String representation,
            boolean keyword, boolean standaloneStatement,
            boolean reserved, TokenType assignsWith,
            String defaultLexeme, TokenCharacter[] characters) {
        this.type = type;
        this.representation = representation;
        this.keyword = keyword;
        this.standaloneStatement = standaloneStatement;
        this.reserved = reserved;
        this.assignsWith = assignsWith;
        this.defaultLexeme = defaultLexeme;
        this.characters = characters;
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

    /**
     * Gets the standard representation of the {@link TokenType}.
     * @return The representation of the token.
     */
    @Override
    public String representation() {
        return this.representation;
    }

    /**
     * Gets whether the {@link TokenType} represents a keyword.
     * @return {@code true} if the token represents a keyword, or {@code false}.
     */
    @Override
    public boolean keyword() {
        return this.keyword;
    }

    /**
     * Gets whether the {@link TokenType} can be used as a standalone statement. This
     * is typically used to indicate a token is not part of an expression statement,
     * but can optionally accept expressions when parsed.
     * @return {@code true} if the token can be used as a standalone statement.
     */
    @Override
    public boolean standaloneStatement() {
        return this.standaloneStatement;
    }

    /**
     * Gets whether the use of this {@link TokenType} is reserved. This is typically used
     * to indicate a token that is not yet supported, but is reserved for future use.
     * @return {@code true} if the token is reserved.
     */
    @Override
    public boolean reserved() {
        return this.reserved;
    }

    /**
     * Gets the {@link TokenType} which this {@link TokenType} assigns with. This is
     * typically used to indicate a token that can be used as an assignment operator.
     * @return The {@link TokenType} which this {@link TokenType} assigns with.
     */
    @Override
    public TokenType assignsWith() {
        return this.assignsWith;
    }

    @Override
    public String defaultLexeme() {
        return this.defaultLexeme;
    }

    @Override
    public TokenCharacter[] characters() {
        return this.characters;
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
