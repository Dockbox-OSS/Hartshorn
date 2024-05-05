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

import org.dockbox.hartshorn.hsl.lexer.SimpleTokenRegistryLexer;
import org.dockbox.hartshorn.hsl.token.type.TokenType;

/**
 * A collection of standard token characters which can be used by the {@link SimpleTokenRegistryLexer}
 * to tokenize a given HSL script. This is also used by the standard {@link TokenType}s to specify their
 * representation.
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public enum DefaultTokenCharacter implements SimpleTokenCharacter {
    LEFT_PAREN('('),
    RIGHT_PAREN(')'),
    LEFT_BRACE('{'),
    RIGHT_BRACE('}'),
    ARRAY_OPEN('['),
    ARRAY_CLOSE(']'),
    COMMA(','),
    DOT('.'),
    MINUS('-'),
    PLUS('+'),
    SEMICOLON(';'),
    SLASH('/'),
    STAR('*'),
    MODULO('%'),
    EQUAL('='),
    BANG('!'),
    GREATER('>'),
    LESS('<'),
    QUESTION_MARK('?'),
    COLON(':'),
    HASH('#'),
    QUOTE('"'),
    SINGLE_QUOTE('\''),
    AMPERSAND('&'),
    PIPE('|'),
    CARET('^'),
    TILDE('~'),
    UNDERSCORE('_'),

    ;

    private final char character;

    DefaultTokenCharacter(char character) {
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
