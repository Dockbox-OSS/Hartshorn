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

import org.dockbox.hartshorn.hsl.token.type.LiteralTokenType;
import org.dockbox.hartshorn.hsl.token.type.TokenType;

/**
 * Utility builder for creating {@link Token} instances based on a given {@link TokenType}. This
 * builder can be used to create tokens with a specific lexeme, literal, line and column, and can be
 * used to create virtual tokens that do not have a specific position in the source code.
 *
 * @see Token
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public class TokenBuilder {

    private final TokenType type;
    private Object literal;
    private String lexeme;
    private int line;
    private int column;

    public TokenBuilder(TokenType type) {
        this.type = type;
    }

    /**
     * Sets the literal value of the token. This is the value that the token represents in the source
     * code, and is used to determine the value of the token when it is a literal token type.
     *
     * @param literal the literal value of the token
     * @return this builder
     *
     * @see Token#literal()
     */
    public TokenBuilder literal(Object literal) {
        this.literal = literal;
        if (this.lexeme == null) {
            this.lexeme = String.valueOf(literal);
        }
        return this;
    }

    /**
     * Sets the lexeme of the token. This is the string that represents the abstract meaning of the
     * token.
     *
     * @param lexeme the lexeme of the token
     * @return this builder
     *
     * @see Token#lexeme()
     */
    public TokenBuilder lexeme(String lexeme) {
        this.lexeme = lexeme;
        return this;
    }

    /**
     * Sets the line number of the token. This is the line in the source code where the token is
     * located.
     *
     * @param line the line number of the token
     * @return this builder
     *
     * @see Token#line()
     */
    public TokenBuilder line(int line) {
        this.line = line;
        return this;
    }

    /**
     * Sets the column number of the token. This is the column in the source code where the token is
     * located.
     *
     * @param column the column number of the token
     * @return this builder
     *
     * @see Token#column()
     */
    public TokenBuilder column(int column) {
        this.column = column;
        return this;
    }

    /**
     * Sets the position of the token based on the position of another token. This is a convenience
     * method that sets the line and column of the token to the line and column of the given token.
     *
     * @param token the token to base the position on
     * @return this builder
     *
     * @see Token#line()
     * @see Token#column()
     */
    public TokenBuilder position(Token token) {
        return this
                .line(token.line())
                .column(token.column());
    }

    /**
     * Sets the position of the token to a virtual position. This is a convenience method that sets
     * the line and column of the token to -1, indicating that the token does not have a specific
     * position in the source code.
     *
     * @return this builder
     *
     * @see Token#line()
     * @see Token#column()
     */
    public TokenBuilder virtual() {
        return this
                .line(-1)
                .column(-1);
    }

    /**
     * Builds the token based on the current state of the builder. If the lexeme is not set, and the
     * token type is not a keyword or an EOF type, an {@link IllegalArgumentException} is thrown. If
     * the type is a keyword, the lexeme is set to the representation of the type. If the type is an
     * EOF type, the lexeme is set to an empty string.
     *
     * @return the token
     *
     * @see Token
     */
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
}
