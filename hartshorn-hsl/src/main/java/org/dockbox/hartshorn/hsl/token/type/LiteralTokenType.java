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
 * Represents the different types of literal tokens that can be used in the HSL language. A literal token is a
 * token that represents a value, such as a number, string, or boolean.
 *
 * @see TokenType
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public enum LiteralTokenType implements EnumTokenType {
    /**
     * Identifier token, representing a name of a variable, function or other declared member
     * in the HSL language.
     */
    IDENTIFIER,
    /**
     * String token, representing a sequence of characters.
     */
    STRING,
    /**
     * Number token, representing a numeric value. This can be an integer or a floating point number.
     */
    NUMBER,
    /**
     * Character token, representing a single character.
     */
    CHAR,
    /**
     * End of file token, representing the end of the input.
     */
    EOF,
    /**
     * Null token, representing the absence of a value.
     */
    NULL("null"),
    /**
     * True token, representing the boolean value 'true'.
     */
    TRUE("true"),
    /**
     * False token, representing the boolean value 'false'.
     */
    FALSE("false"),
    ;

    private final TokenMetaData metaData;

    LiteralTokenType(String defaultLexeme) {
        this.metaData = TokenMetaData.builder(this).defaultLexeme(defaultLexeme).build();
    }

    LiteralTokenType() {
        this.metaData = TokenMetaData.builder(this).build();
    }

    @Override
    public TokenType delegate() {
        return this.metaData;
    }
}
