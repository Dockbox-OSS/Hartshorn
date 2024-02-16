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
 * Represents the different types of member modifiers that can be used in the HSL language. These are used to specify
 * the visibility and behavior of class members and optionally variables.
 *
 * @see TokenType
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public enum MemberModifierTokenType implements EnumTokenType {
    /**
     * 'public' token, representing a public member modifier. This modifier makes the member visible to all other
     * classes.
     */
    PUBLIC,
    /**
     * 'private' token, representing a private member modifier. This modifier makes the member visible only within the
     * class in which it is defined.
     */
    PRIVATE,
    /**
     * 'final' token, representing a final member modifier. This modifier makes the member or variable unchangeable after
     * it has been initialized.
     */
    FINAL,
    ;

    private final TokenMetaData metaData;

    MemberModifierTokenType() {
        this.metaData = TokenMetaData.builder(this)
                .keyword(true)
                .ok();
    }

    @Override
    public TokenType delegate() {
        return metaData;
    }
}
