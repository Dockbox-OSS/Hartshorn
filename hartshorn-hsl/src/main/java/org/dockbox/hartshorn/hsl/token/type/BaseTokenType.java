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

import org.dockbox.hartshorn.hsl.token.DefaultTokenCharacter;
import org.dockbox.hartshorn.hsl.token.TokenCharacter;
import org.dockbox.hartshorn.hsl.token.TokenMetaData;

/**
 * Represents the basic token types that are used in the HSL language. These
 * tokens are often used as building blocks for more complex tokens.
 *
 * @see TokenType
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public enum BaseTokenType implements EnumTokenType {
    /**
     * '?' character, often used to indicate an elvis or ternary expression.
     */
    QUESTION_MARK(DefaultTokenCharacter.QUESTION_MARK),
    /**
     * ':' character, often used to identify a label or to separate
     * ternary expressions.
     */
    COLON(DefaultTokenCharacter.COLON),
    /**
     * ',' character, often used to separate elements in a list or array.
     */
    COMMA(DefaultTokenCharacter.COMMA),
    /**
     * '.' character, often used to access properties of objects, separate
     * namespaces, or to indicate a decimal number.
     */
    DOT(DefaultTokenCharacter.DOT),
    /**
     * ';' character, often used to terminate statements.
     */
    SEMICOLON(DefaultTokenCharacter.SEMICOLON),
    /**
     * '=' character, often used for assignment or comparison.
     */
    EQUAL(DefaultTokenCharacter.EQUAL),
    /**
     * '!' character, often used for evaluation of boolean expressions.
     */
    BANG(DefaultTokenCharacter.BANG),
    ;

    private final TokenMetaData metaData;

    BaseTokenType(TokenCharacter character) {
        this.metaData = TokenMetaData.builder(this)
                .representation(String.valueOf(character.character()))
                .characters(character)
                .build();
    }

    @Override
    public TokenType delegate() {
        return metaData;
    }
}
