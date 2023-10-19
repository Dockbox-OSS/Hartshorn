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

package test.org.dockbox.hartshorn.hsl;

import org.dockbox.hartshorn.hsl.lexer.Comment;
import org.dockbox.hartshorn.hsl.lexer.Lexer;
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
            TokenType.FUNCTION, TokenType.RETURN, TokenType.NATIVE,
            TokenType.TRUE, TokenType.FALSE,
            TokenType.FOR, TokenType.DO, TokenType.WHILE, TokenType.REPEAT,
            TokenType.BREAK, TokenType.CONTINUE,
            TokenType.SUPER, TokenType.THIS,
            TokenType.NULL, TokenType.TEST,
            TokenType.VAR, TokenType.IMPORT,
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
            TokenType.SEMICOLON, TokenType.EQUAL, TokenType.BANG,
            TokenType.MODULO, TokenType.STAR, TokenType.SLASH,
            TokenType.GREATER, TokenType.LESS,
            TokenType.QUESTION_MARK, TokenType.COLON,
    };

    public static Stream<Arguments> tokens() {
        List<Arguments> arguments = new ArrayList<>();
        for (TokenType type : TokenType.values()) {
            if (literals.contains(type)) {
                continue;
            }
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
    void testTokenTypesUseConstantsOfSameName(TokenType type) throws IllegalAccessException, NoSuchFieldException {
        String name = type.name();
        Field field = TokenConstants.class.getField(name);
        // Should be static and public
        char constant = (char) field.get(null);
        Assertions.assertEquals(String.valueOf(constant), type.representation());
    }

    @ParameterizedTest
    @MethodSource("keywords")
    void testKeywordsMatchExpectedList(TokenType type) {
        Collection<TokenType> tokenTypes = TokenType.keywords().values();
        Assertions.assertTrue(tokenTypes.contains(type));
    }

    @ParameterizedTest
    @MethodSource("keywords")
    void testKeywordsHaveCorrectMetaData(TokenType type) {
        Assertions.assertTrue(type.keyword());
    }

    @ParameterizedTest
    @MethodSource("tokens")
    void testCorrectToken(String text, TokenType expected) {
        Lexer lexer = new Lexer(text);
        List<Token> tokens = lexer.scanTokens();

        Assertions.assertNotNull(tokens);
        Assertions.assertEquals(2, tokens.size());

        Token token = tokens.get(0);
        Assertions.assertEquals(expected, token.type());
        Assertions.assertEquals(1, token.line());

        Token eof = tokens.get(1);
        Assertions.assertEquals(TokenType.EOF, eof.type());
    }

    @Test
    void testSingleLineComment() {
        Lexer lexer = new Lexer("# Comment");
        List<Token> tokens = lexer.scanTokens();

        Assertions.assertNotNull(tokens);
        Assertions.assertEquals(1, tokens.size());

        Token token = tokens.get(0);
        Assertions.assertEquals(TokenType.EOF, token.type());

        List<Comment> comments = lexer.comments();
        Assertions.assertNotNull(comments);
        Assertions.assertEquals(1, comments.size());

        Comment comment = comments.get(0);
        // Comments are not trimmed, include whitespace
        Assertions.assertEquals(" Comment", comment.text());
    }
}
