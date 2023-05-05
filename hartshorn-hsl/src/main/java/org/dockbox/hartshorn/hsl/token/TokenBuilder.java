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

public class TokenBuilder {

    private final TokenType type;
    private Object literal;
    private String lexeme;
    private int line;
    private int column;

    public TokenBuilder(final TokenType type) {
        this.type = type;
    }

    public TokenBuilder literal(final Object literal) {
        this.literal = literal;
        return this;
    }

    public TokenBuilder lexeme(final String lexeme) {
        this.lexeme = lexeme;
        return this;
    }

    public TokenBuilder line(final int line) {
        this.line = line;
        return this;
    }

    public TokenBuilder column(final int column) {
        this.column = column;
        return this;
    }

    public TokenBuilder position(final Token token) {
        return this
                .line(token.line())
                .column(token.column());
    }

    public Token build() {
        return new Token(this.type, this.lexeme, this.literal, this.line, this.column);
    }

    public TokenBuilder virtual() {
        return this
                .line(-1)
                .column(-1);
    }
}
