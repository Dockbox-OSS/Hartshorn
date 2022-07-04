/*
 * Copyright 2019-2022 the original author or authors.
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

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Represents a single token which exists within an HSL script. A token is always of
 * a valid {@link TokenType}.
 *
 * @author Guus Lieben
 * @since 22.4
 */
public class Token {

    private final TokenType type;
    private final Object literal;
    private final int line;
    private String lexeme;

    public Token(final TokenType type, final String lexeme, final int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = null;
        this.line = line;
    }

    public Token(final TokenType type, final String lexeme, final Object literal, final int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
    }

    /**
     * Adds the lexical meaning of the given token to the lexical meaning of
     * this token.
     * @param token The token of which the lexical meaning is to be concatenated.
     */
    public void concat(final Token token) {
        if(token == null) {
            return;
        }
        this.lexeme += token.lexeme;
    }

    /**
     * Gets the lexical meaning of this token.
     * @return The lexical meaning of this token.
     */
    public String lexeme() {
        return this.lexeme;
    }

    /**
     * Gets the literal value of this token, this is commonly used for
     * {@link TokenType}s which are literal types.
     *
     * @return The literal value of this token.
     */
    @Nullable
    public Object literal() {
        return this.literal;
    }

    /**
     * Gets the type of this token.
     * @return The type of this token.
     */
    public TokenType type() {
        return this.type;
    }

    /**
     * Gets the line at which the token is located.
     * @return The line of this token.
     */
    public int line() {
        return this.line;
    }

    public String toString() {
        return "Token[%s @ line %d = %s / %s]".formatted(this.type, this.line, this.lexeme, this.literal);
    }
}
