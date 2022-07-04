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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Definitions for all standard token types. Token types always carry a single
 * {@link TokenMetaData} instance, providing additional information about the
 * token type.
 *
 * @author Guus Lieben
 * @since 22.4
 */
public enum TokenType {
    // Literals
    IDENTIFIER, STRING, NUMBER, CHAR, EOF,

    // Single-character tokens
    LEFT_PAREN(TokenConstants.LEFT_PAREN),
    RIGHT_PAREN(TokenConstants.RIGHT_PAREN),
    LEFT_BRACE(TokenConstants.LEFT_BRACE),
    RIGHT_BRACE(TokenConstants.RIGHT_BRACE),
    ARRAY_OPEN(TokenConstants.ARRAY_OPEN),
    ARRAY_CLOSE(TokenConstants.ARRAY_CLOSE),
    COMMA(TokenConstants.COMMA),
    DOT(TokenConstants.DOT),
    MINUS(TokenConstants.MINUS),
    PLUS(TokenConstants.PLUS),
    SEMICOLON(TokenConstants.SEMICOLON),
    SLASH(TokenConstants.SLASH),
    STAR(TokenConstants.STAR),
    EQUAL(TokenConstants.EQUAL),
    BANG(TokenConstants.BANG),
    GREATER(TokenConstants.GREATER),
    LESS(TokenConstants.LESS),
    QUESTION_MARK(TokenConstants.QUESTION_MARK),
    COLON(TokenConstants.COLON),
    XOR(TokenConstants.CARET),
    BITWISE_AND(TokenConstants.AMPERSAND),
    BITWISE_OR(TokenConstants.PIPE),
    COMPLEMENT(TokenConstants.TILDE),

    // Two character tokens combining single character tokens
    ELVIS(builder -> builder.combines(QUESTION_MARK, COLON).ok()),
    EQUAL_EQUAL(builder -> builder.repeats(EQUAL).ok()),
    BANG_EQUAL(builder -> builder.combines(BANG, EQUAL).ok()),
    GREATER_EQUAL(builder -> builder.combines(GREATER, EQUAL).ok()),
    LESS_EQUAL(builder -> builder.combines(LESS, EQUAL).ok()),
    PLUS_PLUS(builder -> builder.repeats(PLUS).ok()),
    MINUS_MINUS(builder -> builder.repeats(MINUS).ok()),
    SHIFT_RIGHT(builder -> builder.repeats(GREATER).ok()),
    SHIFT_LEFT(builder -> builder.repeats(LESS).ok()),
    LOGICAL_SHIFT_RIGHT(builder -> builder.combines(GREATER, GREATER, GREATER).ok()),
    AND(builder -> builder.repeats(BITWISE_AND).ok()),
    OR(builder -> builder.repeats(BITWISE_OR).ok()),

    // Keywords,
    PREFIX(builder -> builder.keyword(true).ok()),
    INFIX(builder -> builder.keyword(true).ok()),
    CLASS(builder -> builder.keyword(true).ok()),
    FUN(builder -> builder.keyword(true).ok()),
    EXTENDS(builder -> builder.keyword(true).ok()),
    ELSE(builder -> builder.keyword(true).ok()),
    TRUE(builder -> builder.keyword(true).ok()),
    FALSE(builder -> builder.keyword(true).ok()),
    FOR(builder -> builder.keyword(true).ok()),
    SUPER(builder -> builder.keyword(true).ok()),
    THIS(builder -> builder.keyword(true).ok()),
    VAR(builder -> builder.keyword(true).ok()),
    NULL(builder -> builder.keyword(true).ok()),
    ARRAY(builder -> builder.keyword(true).ok()),
    NATIVE(builder -> builder.keyword(true).ok()),

    // Standalone statements
    IF(builder -> builder.keyword(true).standaloneStatement(true).ok()),
    REPEAT(builder -> builder.keyword(true).standaloneStatement(true).ok()),
    DO(builder -> builder.keyword(true).standaloneStatement(true).ok()),
    WHILE(builder -> builder.keyword(true).standaloneStatement(true).ok()),
    BREAK(builder -> builder.keyword(true).standaloneStatement(true).ok()),
    CONTINUE(builder -> builder.keyword(true).standaloneStatement(true).ok()),
    RETURN(builder -> builder.keyword(true).standaloneStatement(true).ok()),
    PRINT(builder -> builder.keyword(true).standaloneStatement(true).ok()),
    TEST(builder -> builder.keyword(true).standaloneStatement(true).ok()),
    MODULE(builder -> builder.keyword(true).standaloneStatement(true).ok()),
    ;

    private final TokenMetaData metaData;

    /**
     * Creates a new {@link TokenType} with the standard representation. The created {@link TokenType}
     * will not be a keyword or standalone statement.
     */
    TokenType() {
        this(TokenMetaDataBuilder::ok);
    }

    TokenType(final char representation) {
        this(representation + "");
    }

    /**
     * Creates a new {@link TokenType} with the given static representation. The created {@link TokenType}
     * will not be a keyword or standalone statement.
     * @param representation
     */
    TokenType(final String representation) {
        this(b -> b.representation(representation).ok());
    }

    /**
     * Creates a new {@link TokenType} and allows the instantiating source to customize the metadata of
     * the new {@link TokenType}.
     * @param builder
     */
    TokenType(final Function<TokenMetaDataBuilder, TokenMetaData> builder) {
        this.metaData = builder.apply(TokenMetaData.builder(this));
    }

    /**
     * @see TokenMetaData#representation()
     */
    public String representation() {
        return this.metaData.representation();
    }

    /**
     * @see TokenMetaData#keyword()
     */
    public boolean keyword() {
        return this.metaData.keyword();
    }

    /**
     * @see TokenMetaData#standaloneStatement()
     */
    public boolean standaloneStatement() {
        return this.metaData.standaloneStatement();
    }

    /**
     * Gets all token types which represent keywords, identified by their representation.
     * @return All token types which represent keywords.
     */
    public static Map<String, TokenType> keywords() {
        final Map<String, TokenType> keywords = new ConcurrentHashMap<>();
        for (final TokenType tokenType : TokenType.values()) {
            if (tokenType.keyword()) {
                keywords.put(tokenType.representation(), tokenType);
            }
        }
        return Map.copyOf(keywords);
    }
}
