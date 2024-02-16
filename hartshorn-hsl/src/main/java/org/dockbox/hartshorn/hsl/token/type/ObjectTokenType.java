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
 * Represents the different types of instance tokens that give context to the current object instance in
 * the HSL language. These tokens are used to refer to the current object instance or the super class
 * of the current object instance.
 *
 * @see TokenType
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public enum ObjectTokenType implements EnumTokenType {
    /**
     * 'super' token, representing the super class of the current object instance.
     */
    SUPER,
    /**
     * 'this' token, representing the current object instance.
     */
    THIS,
    ;

    private final TokenMetaData metaData;

    ObjectTokenType() {
        this.metaData = TokenMetaData.builder(this)
                .keyword(true)
                .ok();
    }

    @Override
    public TokenType delegate() {
        return metaData;
    }
}
