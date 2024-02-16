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
import org.dockbox.hartshorn.hsl.token.TokenMetaDataBuilder;
import org.dockbox.hartshorn.util.Customizer;

/**
 * Represents the different types of tokens that are used to assert or test conditions in a script.
 *
 * @see TokenType
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public enum AssertTokenType implements EnumTokenType {
    /**
     * Represents the 'assert' keyword for a standalone statement.
     *
     * <p>Reserved for future use, but not yet implemented.
     */
    ASSERT(builder -> builder.keyword(true).standaloneStatement(true).reserved(true)),
    /**
     * Represents the 'expect' keyword for a block statement.
     */
    TEST(builder -> builder.keyword(true).standaloneStatement(true)),
    ;

    private final TokenMetaData metaData;

    AssertTokenType(Customizer<TokenMetaDataBuilder> customizer) {
        TokenMetaDataBuilder builder = TokenMetaData.builder(this);
        customizer.configure(builder);
        this.metaData = builder.build();
    }

    @Override
    public TokenType delegate() {
        return metaData;
    }
}
