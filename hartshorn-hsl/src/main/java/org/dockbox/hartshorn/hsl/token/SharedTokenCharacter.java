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
 * Represents the default characters that are always available to represent certain tokens in the
 * HSL language. This covers whitespace characters and the null character.
 *
 * @since 0.6.0
 *
 * @see TokenCharacter
 *
 * @author Guus Lieben
 */
public enum SharedTokenCharacter implements SimpleTokenCharacter {
    /**
     * The space character. By default, this will be ignored by the lexer.
     */
    SPACE(' '),
    /**
     * The tab character. By default, this will be ignored by the lexer.
     */
    TAB('\t'),
    /**
     * The newline character. By default, this will be used to determine line numbers.
     */
    NEWLINE('\n'),
    /**
     * The carriage return character. By default, this will be ignored by the lexer.
     */
    CARRIAGE_RETURN('\r'),
    /**
     * The null character. By default, this will be used to represent characters that
     * are looked up outside the bounds of a string.
     */
    NULL('\0'),
    ;

    private final char character;

    SharedTokenCharacter(char character) {
        this.character = character;
    }

    @Override
    public char character() {
        return this.character;
    }

    @Override
    public boolean isStandaloneCharacter() {
        return true;
    }
}
