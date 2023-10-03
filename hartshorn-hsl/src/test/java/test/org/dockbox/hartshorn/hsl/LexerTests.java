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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.dockbox.hartshorn.hsl.lexer.Comment;
import org.dockbox.hartshorn.hsl.lexer.DefaultTokenSetLexer;
import org.dockbox.hartshorn.hsl.lexer.Lexer;
import org.dockbox.hartshorn.hsl.token.DefaultTokenRegistry;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenRegistry;
import org.dockbox.hartshorn.hsl.token.type.TokenType;
import org.dockbox.hartshorn.hsl.token.type.LiteralTokenType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import test.org.dockbox.hartshorn.hsl.interpreter.InterpreterTestHelper;

public class LexerTests {

    private static final TokenType[] keywords;
    private static final Set<TokenType> literals = Set.of(LiteralTokenType.values());

    static {
        keywords = InterpreterTestHelper.defaultTokenSet().tokenTypes(TokenType::keyword).toArray(TokenType[]::new);
    }

    public static Stream<Arguments> tokens() {
        final List<Arguments> arguments = new ArrayList<>();
        DefaultTokenRegistry tokenSet = DefaultTokenRegistry.createDefault();
        Set<TokenType> nonLiteralTokens = tokenSet.tokenTypes(type -> !literals.contains(type));
        for (final TokenType type : nonLiteralTokens) {
            arguments.add(Arguments.of(type.representation(), type));
        }
        arguments.add(Arguments.of("12.0", LiteralTokenType.NUMBER));
        arguments.add(Arguments.of("foobar", LiteralTokenType.IDENTIFIER));
        arguments.add(Arguments.of("\"foo\"", LiteralTokenType.STRING));
        arguments.add(Arguments.of("'a'", LiteralTokenType.CHAR));
        return arguments.stream();
    }

    public static Stream<Arguments> keywords() {
        return Arrays.stream(keywords).map(Arguments::of);
    }

    @ParameterizedTest
    @MethodSource("keywords")
    void testKeywordsMatchExpectedList(final TokenType type) {
        TokenRegistry tokenRegistry = InterpreterTestHelper.defaultTokenSet();
        final Set<TokenType> tokenTypes = tokenRegistry.tokenTypes(TokenType::keyword);
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
        final Lexer lexer = new DefaultTokenSetLexer(text, InterpreterTestHelper.defaultTokenSet());
        final List<Token> tokens = lexer.scanTokens();

        Assertions.assertNotNull(tokens);
        Assertions.assertEquals(2, tokens.size());

        final Token token = tokens.get(0);
        Assertions.assertEquals(expected, token.type());
        Assertions.assertEquals(1, token.line());

        final Token eof = tokens.get(1);
        Assertions.assertEquals(LiteralTokenType.EOF, eof.type());
    }

    @Test
    void testSingleLineComment() {
        final Lexer lexer = new DefaultTokenSetLexer("# Comment", InterpreterTestHelper.defaultTokenSet());
        final List<Token> tokens = lexer.scanTokens();

        Assertions.assertNotNull(tokens);
        Assertions.assertEquals(1, tokens.size());

        final Token token = tokens.get(0);
        Assertions.assertEquals(LiteralTokenType.EOF, token.type());

        final List<Comment> comments = lexer.comments();
        Assertions.assertNotNull(comments);
        Assertions.assertEquals(1, comments.size());

        final Comment comment = comments.get(0);
        // Comments are not trimmed, include whitespace
        Assertions.assertEquals(" Comment", comment.text());
    }
}
