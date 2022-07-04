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

    public void concat(final Token token) {
        if(token == null) {
            return;
        }
        this.lexeme += token.lexeme;
    }

    public String lexeme() {
        return this.lexeme;
    }

    public Object literal() {
        return this.literal;
    }

    public TokenType type() {
        return this.type;
    }

    public int line() {
        return this.line;
    }

    public String toString() {
        return "Token[%s @ line %d = %s / %s]".formatted(this.type, this.line, this.lexeme, this.literal);
    }
}
