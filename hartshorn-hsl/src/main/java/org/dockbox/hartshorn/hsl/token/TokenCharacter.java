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

package org.dockbox.hartshorn.hsl.token;

import org.dockbox.hartshorn.hsl.token.type.TokenType;

/**
 * Represents a single character in a token type definition. This is used to determine the type of
 * character and how it should be handled.
 *
 * @see TokenType#characters()
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public interface TokenCharacter {

    /**
     * Returns the character that this token character represents.
     *
     * @return the character
     */
    char character();

    /**
     * Returns whether this character is single digit, often meaning it is a number between 0 and 9,
     * though implementations may vary.
     *
     * @return true if the character is whitespace, false otherwise
     */
    boolean isDigit();

    /**
     * Returns whether this character is a letter, meaning it is a character between a-z or A-Z,
     * though implementations may vary.
     *
     * @return true if the character is whitespace, false otherwise
     */
    boolean isAlpha();

    /**
     * Returns whether this character is alphanumeric, meaning it is either a letter or a number. Often
     * this is used to determine
     *
     * @return true if the character is alphanumeric, false otherwise
     */
    boolean isAlphaNumeric();

    /**
     * Returns whether this character is a standalone character, meaning it is not part of a
     * larger dynamically named token like literal values.
     *
     * @return true if the character is standalone, false otherwise
     */
    boolean isStandaloneCharacter();
}
