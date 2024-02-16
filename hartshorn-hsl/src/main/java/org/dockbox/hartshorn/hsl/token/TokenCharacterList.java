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

/**
 * A collection of characters that are used for basic literals in the HSL language.
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public interface TokenCharacterList {

    /**
     * The character that represents a null value in HSL. This is often used by
     * lexers to denote the absence of a valid character at a certain position.
     * This typically indicates that the lexer exceeded the input string.
     *
     * @return the character that represents the null value in HSL.
     */
    TokenCharacter nullCharacter();

    /**
     * The character that represents the quotes around a string in HSL. This is
     * often used by lexers to denote the start and end of a string.
     *
     * @return the character that represents the true value in HSL.
     */
    TokenCharacter quoteCharacter();

    /**
     * The character that represents the quotes around a single character in HSL.
     * This is often used by lexers to denote the start and end of a character.
     *
     * @return the character that represents the true value in HSL.
     */
    TokenCharacter charCharacter();

    /**
     * The character that represents a separator between numbers in HSL. This is
     * ignored by the lexer, but can be used by script authors to improve the
     * readability of long numbers.
     *
     * @return the character that represents the number separator in HSL.
     */
    TokenCharacter numberSeparator();

    /**
     * The character that represents a delimiter between decimal numbers in HSL.
     *
     * @return the character that represents the number delimiter in HSL.
     */
    TokenCharacter numberDelimiter();

}
