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

import java.util.Locale;

/**
 * Utility-class to easily build new {@link TokenMetaData} instances.
 *
 * @author Guus Lieben
 * @since 22.4
 */
public class TokenMetaDataBuilder {

    final TokenType type;
    String representation;
    boolean keyword;
    boolean standaloneStatement;

    TokenMetaDataBuilder(final TokenType type) {
        this.type = type;
        this.representation = type.name().toLowerCase(Locale.ROOT);
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

    public TokenMetaDataBuilder repeats(final TokenType type) {
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

    public TokenMetaData ok() {
        return new TokenMetaData(this);
    }
}
