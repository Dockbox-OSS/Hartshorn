/*
 * Copyright 2019-2023 the original author or authors.
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

import org.dockbox.hartshorn.hsl.lexer.AbstractTokenSetLexer;
import org.dockbox.hartshorn.hsl.token.type.TokenType;

/**
 * A collection of standard token characters which can be used by the {@link AbstractTokenSetLexer}
 * to tokenize a given HSL script. This is also used by the standard {@link TokenType}s to specify their
 * representation.
 *
 * @author Guus Lieben
 * @since 0.4.12
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
    SPACE(' '),
    SINGLE_QUOTE('\''),
    AMPERSAND('&'),
    PIPE('|'),
    CARET('^'),
    TILDE('~'),
    UNDERSCORE('_'),
    TAB('\t'),
    NEWLINE('\n'),
    CARRIAGE_RETURN('\r'),
    NULL('\0'),

    ;

    private final char character;

    DefaultTokenCharacter(final char character) {
        this.character = character;
    }

    @Override
    public char character() {
        return this.character;
    }

    public static DefaultTokenCharacter of(char character) {
        for (final DefaultTokenCharacter value : DefaultTokenCharacter.values()) {
            if (value.character() == character) {
                return value;
            }
        }
        return null;
    }
}
