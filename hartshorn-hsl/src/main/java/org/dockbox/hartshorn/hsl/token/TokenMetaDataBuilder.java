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
 * Utility-class to easily build new {@link TokenMetaData} instances.
 *
 * @author Guus Lieben
 * @since 0.4.12
 */
public class TokenMetaDataBuilder {

    private final TokenType type;
    private String representation;
    private boolean keyword;
    private boolean standaloneStatement;
    private boolean reserved;
    private TokenType assignsWith;
    private String defaultLexeme;

    TokenMetaDataBuilder(final TokenType type) {
        this.type = type;
        this.representation = type.tokenName();
    }

    public TokenMetaDataBuilder representation(final String representation) {
        this.representation = representation;
        return this;
    }

    public TokenMetaDataBuilder combines(final TokenType... types) {
        final StringBuilder combined = new StringBuilder();
        for (final TokenType type : types) {
            combined.append(type.representation());
        }
        this.representation = combined.toString();
        return this;
    }

    public TokenMetaDataBuilder combines(final TokenCharacter... types) {
        final StringBuilder combined = new StringBuilder();
        for (final TokenCharacter type : types) {
            combined.append(type.character());
        }
        this.representation = combined.toString();
        return this;
    }

    public TokenMetaDataBuilder repeats(final TokenType type) {
        return this.combines(type, type);
    }

    public TokenMetaDataBuilder repeats(final TokenCharacter type) {
        return this.combines(type, type);
    }

    public TokenMetaDataBuilder keyword(final boolean keyword) {
        this.keyword = keyword;
        return this;
    }

    public TokenMetaDataBuilder standaloneStatement(final boolean standaloneStatement) {
        this.standaloneStatement = standaloneStatement;
        return this;
    }

    public TokenMetaDataBuilder reserved(final boolean reserved) {
        this.reserved = reserved;
        return this;
    }

    public TokenMetaDataBuilder assignsWith(final TokenType assignsWith) {
        this.assignsWith = assignsWith;
        return this;
    }

    public TokenMetaDataBuilder defaultLexeme(final String defaultLexeme) {
        this.defaultLexeme = defaultLexeme;
        return this;
    }

    public TokenMetaData ok() {
        return new TokenMetaData(this.type, this.representation, this.keyword, this.standaloneStatement, this.reserved, this.assignsWith, this.defaultLexeme);
    }
}
