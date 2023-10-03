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

import org.dockbox.hartshorn.hsl.token.type.LiteralTokenType;
import org.dockbox.hartshorn.hsl.token.type.TokenType;

public class TokenBuilder {

    private final TokenType type;
    private Object literal;
    private String lexeme;
    private int line;
    private int column;

    public TokenBuilder(TokenType type) {
        this.type = type;
    }

    public TokenBuilder literal(Object literal) {
        this.literal = literal;
        if (this.lexeme == null) {
            this.lexeme = String.valueOf(literal);
        }
        return this;
    }

    public TokenBuilder lexeme(String lexeme) {
        this.lexeme = lexeme;
        return this;
    }

    public TokenBuilder line(int line) {
        this.line = line;
        return this;
    }

    public TokenBuilder column(int column) {
        this.column = column;
        return this;
    }

    public TokenBuilder position(Token token) {
        return this
                .line(token.line())
                .column(token.column());
    }

    public Token build() {
        if (lexeme == null) {
            if (this.type.keyword()) {
                this.lexeme = this.type.representation();
            }
            else if (this.type == LiteralTokenType.EOF) {
                this.lexeme = "";
            }
            else {
                throw new IllegalArgumentException("Cannot create a token of a non-keyword type without a lexeme");
            }
        }
        return new Token(this.type, this.lexeme, this.literal, this.line, this.column);
    }

    public TokenBuilder virtual() {
        return this
                .line(-1)
                .column(-1);
    }
}
