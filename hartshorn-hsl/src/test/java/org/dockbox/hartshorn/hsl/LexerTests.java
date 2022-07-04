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

package org.dockbox.hartshorn.hsl;

import org.dockbox.hartshorn.hsl.callable.ErrorReporter;
import org.dockbox.hartshorn.hsl.lexer.Comment;
import org.dockbox.hartshorn.hsl.lexer.Lexer;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenConstants;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class LexerTests {

    private static final TokenType[] keywords = {
            TokenType.PREFIX, TokenType.INFIX,
            TokenType.CLASS, TokenType.EXTENDS,
            TokenType.IF, TokenType.ELSE,
            TokenType.FUN, TokenType.RETURN, TokenType.NATIVE,
            TokenType.TRUE, TokenType.FALSE,
            TokenType.FOR, TokenType.DO, TokenType.WHILE, TokenType.REPEAT,
            TokenType.BREAK, TokenType.CONTINUE,
            TokenType.SUPER, TokenType.THIS,
            TokenType.NULL, TokenType.ARRAY,
            TokenType.VAR, TokenType.MODULE,
            TokenType.PRINT, TokenType.TEST,
    };

    private static final List<TokenType> literals = List.of(
            TokenType.IDENTIFIER,
            TokenType.STRING,
            TokenType.NUMBER,
            TokenType.CHAR,
            TokenType.EOF);

    private static final TokenType[] singleTokens = {
            TokenType.LEFT_PAREN, TokenType.RIGHT_PAREN,
            TokenType.LEFT_BRACE, TokenType.RIGHT_BRACE,
            TokenType.ARRAY_OPEN, TokenType.ARRAY_CLOSE,
            TokenType.COMMA, TokenType.DOT,
            TokenType.MINUS, TokenType.PLUS,
            TokenType.SEMICOLON, TokenType.SLASH,
            TokenType.STAR, TokenType.EQUAL, TokenType.BANG,
            TokenType.GREATER, TokenType.LESS,
            TokenType.QUESTION_MARK, TokenType.COLON,
    };

    public static Stream<Arguments> tokens() {
        final List<Arguments> arguments = new ArrayList<>();
        for (final TokenType type : TokenType.values()) {
            if (literals.contains(type)) continue;
            arguments.add(Arguments.of(type.representation(), type));
        }
        arguments.add(Arguments.of("12.0", TokenType.NUMBER));
        arguments.add(Arguments.of("foobar", TokenType.IDENTIFIER));
        arguments.add(Arguments.of("\"foo\"", TokenType.STRING));
        arguments.add(Arguments.of("'a'", TokenType.CHAR));
        return arguments.stream();
    }

    public static Stream<Arguments> keywords() {
        return Arrays.stream(keywords).map(Arguments::of);
    }

    public static Stream<Arguments> singleCharacterTokens() {
        return Arrays.stream(singleTokens).map(Arguments::of);
    }

    @ParameterizedTest
    @MethodSource("singleCharacterTokens")
    void testTokenTypesUseConstantsOfSameName(final TokenType type) throws IllegalAccessException, NoSuchFieldException {
        final String name = type.name();
        final Field field = TokenConstants.class.getField(name);
        // Should be static and public
        final char constant = (char) field.get(null);
        Assertions.assertEquals("" + constant, type.representation());
    }

    @ParameterizedTest
    @MethodSource("keywords")
    void testKeywordsMatchExpectedList(final TokenType type) {
        final Collection<TokenType> tokenTypes = TokenType.keywords().values();
        Assertions.assertTrue(tokenTypes.contains(type));
    }

    @ParameterizedTest
    @MethodSource("keywords")
    void testKeywordsHaveCorrectMetaData(final TokenType type) {
        Assertions.assertTrue(type.keyword());
    }

    @ParameterizedTest
    @MethodSource("tokens")
    void testCorrectToken(final String text, final TokenType expected) {
        final Lexer lexer = new Lexer(text, this.errorReporter());
        final List<Token> tokens = lexer.scanTokens();

        Assertions.assertNotNull(tokens);
        Assertions.assertEquals(2, tokens.size());

        final Token token = tokens.get(0);
        Assertions.assertEquals(expected, token.type());
        Assertions.assertEquals(1, token.line());

        final Token eof = tokens.get(1);
        Assertions.assertEquals(TokenType.EOF, eof.type());
    }

    @Test
    void testSingleLineComment() {
        final Lexer lexer = new Lexer("# Comment", this.errorReporter());
        final List<Token> tokens = lexer.scanTokens();

        Assertions.assertNotNull(tokens);
        Assertions.assertEquals(1, tokens.size());

        final Token token = tokens.get(0);
        Assertions.assertEquals(TokenType.EOF, token.type());

        final List<Comment> comments = lexer.comments();
        Assertions.assertNotNull(comments);
        Assertions.assertEquals(1, comments.size());

        final Comment comment = comments.get(0);
        // Comments are not trimmed, include whitespace
        Assertions.assertEquals(" Comment", comment.text());
    }

    ErrorReporter errorReporter() {
        return new ErrorReporter() {
            @Override
            public void error(final Phase phase, final int line, final String message) {
                Assertions.fail("Error reported: " + message);
            }

            @Override
            public void error(final Phase phase, final Token token, final String message) {
                Assertions.fail("Error reported: " + message);
            }

            @Override
            public void clear() {
                // Do nothing
            }
        };
    }

}
