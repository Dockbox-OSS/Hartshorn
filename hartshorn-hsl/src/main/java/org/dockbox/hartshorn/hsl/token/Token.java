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

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.hsl.ast.ASTNode;
import org.dockbox.hartshorn.hsl.token.type.LiteralTokenType;
import org.dockbox.hartshorn.hsl.token.type.TokenType;

/**
 * Represents a single token which exists within an HSL script. A token is always of
 * a valid {@link TokenType}.
 *
 * <p>Within the context of the HSL language, a token is a single unit of meaning. For
 * example, the token {@code "Hello"} is a single token of type {@link LiteralTokenType#STRING}
 * with the literal value {@code "Hello"}. Tokens are used to build up the AST of a
 * script, which can then be parsed into a series of statements.
 *
 * <p>Tokens will always have a {@link TokenType} and a {@link #lexeme()}. The lexeme
 *
 *
 * @author Guus Lieben
 * @since 0.4.12
 */
public class Token extends ASTNode {

    private final TokenType type;
    private final Object literal;
    private String lexeme;

    public Token(TokenType type, String lexeme, int line, int column) {
        this(type, lexeme, null, line, column);
    }

    public Token(TokenType type, String lexeme, Object literal, int line, int column) {
        super(line, column);
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
    }

    public static TokenBuilder of(TokenType type) {
        return new TokenBuilder(type).lexeme(type.defaultLexeme());
    }

    public static TokenBuilder of(TokenType type, String lexeme) {
        return new TokenBuilder(type).lexeme(lexeme);
    }

    /**
     * Adds the lexical meaning of the given token to the lexical meaning of
     * this token.
     * @param token The token of which the lexical meaning is to be concatenated.
     */
    public void concat(Token token) {
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

    public String toString() {
        return "Token[%s @ %d:%d = %s / %s]".formatted(this.type, this.line(), this.column(), this.lexeme, this.literal);
    }
}
