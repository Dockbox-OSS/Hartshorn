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

/**
 * Represents the different types of variable definition tokens that can be used
 * in the HSL language.
 *
 * @see TokenType
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public enum VariableTokenType implements EnumTokenType {
    /**
     * 'var' token, representing the declaration of a variable in a variable declaration statement.
     */
    VAR,
    ;

    private final TokenMetaData metaData;

    VariableTokenType() {
        this.metaData = TokenMetaData.builder(this)
                .keyword(true)
                .standaloneStatement(true)
                .build();
    }

    @Override
    public TokenType delegate() {
        return metaData;
    }
}
