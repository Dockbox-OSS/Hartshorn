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

/**
 * A collection of standard token characters which can be used by the {@link org.dockbox.hartshorn.hsl.lexer.Lexer}
 * to tokenize a given HSL script. This is also used by the standard {@link TokenType}s to specify their
 * representation.
 *
 * @author Guus Lieben
 * @since 22.4
 */
public class TokenConstants {
    public static final char LEFT_PAREN = '(';
    public static final char RIGHT_PAREN = ')';
    public static final char LEFT_BRACE = '{';
    public static final char RIGHT_BRACE = '}';
    public static final char ARRAY_OPEN = '[';
    public static final char ARRAY_CLOSE = ']';
    public static final char COMMA = ',';
    public static final char DOT = '.';
    public static final char MINUS = '-';
    public static final char PLUS = '+';
    public static final char SEMICOLON = ';';
    public static final char SLASH = '/';
    public static final char STAR = '*';
    public static final char MODULO = '%';
    public static final char EQUAL = '=';
    public static final char BANG = '!';
    public static final char GREATER = '>';
    public static final char LESS = '<';
    public static final char QUESTION_MARK = '?';
    public static final char COLON = ':';
    public static final char HASH = '#';
    public static final char QUOTE = '"';
    public static final char SPACE = ' ';
    public static final char SINGLE_QUOTE = '\'';
    public static final char AMPERSAND = '&';
    public static final char PIPE = '|';
    public static final char CARET = '^';
    public static final char TILDE = '~';
}
