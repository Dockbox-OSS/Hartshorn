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

package test.org.dockbox.hartshorn.hsl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.lexer.Comment;
import org.dockbox.hartshorn.hsl.lexer.Lexer;
import org.dockbox.hartshorn.hsl.lexer.SimpleTokenRegistryLexer;
import org.dockbox.hartshorn.hsl.token.DefaultTokenCharacter;
import org.dockbox.hartshorn.hsl.token.DefaultTokenRegistry;
import org.dockbox.hartshorn.hsl.token.SimpleTokenCharacter;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenCharacter;
import org.dockbox.hartshorn.hsl.token.TokenMetaData;
import org.dockbox.hartshorn.hsl.token.type.ArithmeticTokenType;
import org.dockbox.hartshorn.hsl.token.type.BitwiseTokenType;
import org.dockbox.hartshorn.hsl.token.type.ConditionTokenType;
import org.dockbox.hartshorn.hsl.token.type.EnumTokenType;
import org.dockbox.hartshorn.hsl.token.type.LiteralTokenType;
import org.dockbox.hartshorn.hsl.token.type.TokenType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import test.org.dockbox.hartshorn.hsl.interpreter.InterpreterTestHelper;

public class LexerTests {

    public static Stream<Arguments> tokens() {
        final List<Arguments> arguments = new ArrayList<>();
        DefaultTokenRegistry tokenRegistry = DefaultTokenRegistry.createDefault();
        Set<TokenType> nonLiteralTokens = tokenRegistry.tokenTypes(type -> {
            return !(type instanceof LiteralTokenType || tokenRegistry.comments().commentType(type).present());
        });
        for (final TokenType type : nonLiteralTokens) {
            arguments.add(Arguments.of(type.representation(), type));
        }
        arguments.add(Arguments.of("12.0", LiteralTokenType.NUMBER));
        arguments.add(Arguments.of("foobar", LiteralTokenType.IDENTIFIER));
        arguments.add(Arguments.of("\"foo\"", LiteralTokenType.STRING));
        arguments.add(Arguments.of("'a'", LiteralTokenType.CHAR));
        return arguments.stream();
    }

    @ParameterizedTest
    @MethodSource("tokens")
    void testCorrectToken(String text, TokenType expected) {
        Lexer lexer = new SimpleTokenRegistryLexer(text, InterpreterTestHelper.defaultTokenSet());
        List<Token> tokens = lexer.scanTokens();

        Assertions.assertNotNull(tokens);
        Assertions.assertEquals(2, tokens.size());

        Token token = tokens.getFirst();
        Assertions.assertEquals(expected, token.type());
        Assertions.assertEquals(1, token.line());

        Token eof = tokens.get(1);
        Assertions.assertEquals(LiteralTokenType.EOF, eof.type());
    }

    @Test
    void testSingleLineComment() {
        Lexer lexer = new SimpleTokenRegistryLexer("# Comment", InterpreterTestHelper.defaultTokenSet());
        List<Token> tokens = lexer.scanTokens();

        Assertions.assertNotNull(tokens);
        Assertions.assertEquals(1, tokens.size());

        Token token = tokens.getFirst();
        Assertions.assertEquals(LiteralTokenType.EOF, token.type());

        List<Comment> comments = lexer.comments();
        Assertions.assertNotNull(comments);
        Assertions.assertEquals(1, comments.size());

        Comment comment = comments.getFirst();
        // Comments are not trimmed, include whitespace
        Assertions.assertEquals(" Comment", comment.text());
    }

    @Test
    void testCombinedOperatorsAreParsedCorrectly() {
        // No such operator (logical shift left), so should be parsed as '1 << < 2' (1 shift left, less than 2).
        // While this isn't valid code for HSL, it's a good test to see if the lexer is working as expected.
        final Lexer lexer = new SimpleTokenRegistryLexer("1 <<< 2", InterpreterTestHelper.defaultTokenSet());
        List<Token> tokens = lexer.scanTokens();
        Assertions.assertSame(5, tokens.size());
        Assertions.assertEquals(LiteralTokenType.NUMBER, tokens.get(0).type());
        Assertions.assertEquals(BitwiseTokenType.SHIFT_LEFT, tokens.get(1).type());
        Assertions.assertEquals(ConditionTokenType.LESS, tokens.get(2).type());
        Assertions.assertEquals(LiteralTokenType.NUMBER, tokens.get(3).type());
        Assertions.assertEquals(LiteralTokenType.EOF, tokens.get(4).type());
    }

    @Test
    void testIncompleteTokenStepsBackToParent() {
        DefaultTokenRegistry registry = DefaultTokenRegistry.createDefault();
        registry.addTokens(QuadrupleToken.QUADRUPLE_DASH);

        // No token for triple dash, and quadruple dash is incomplete, so should match back based on parent
        // in token graph (from most specific to least specific). This should result in two tokens, one for
        // the double dash (MINUS_MINUS), and one for the single dash (MINUS).
        final Lexer lexer = new SimpleTokenRegistryLexer("---", registry);
        List<Token> tokens = lexer.scanTokens();

        Assertions.assertSame(3, tokens.size());
        Assertions.assertEquals(ArithmeticTokenType.MINUS_MINUS, tokens.get(0).type());
        Assertions.assertEquals(ArithmeticTokenType.MINUS, tokens.get(1).type());
        Assertions.assertEquals(LiteralTokenType.EOF, tokens.get(2).type());
    }

    @Test
    void testIncompleteInvalidTokenFails() {
        DefaultTokenRegistry registry = DefaultTokenRegistry.createDefault();
        registry.addTokens(QuadrupleToken.QUADRUPLE_AT);

        final Lexer lexer = new SimpleTokenRegistryLexer("@@@", registry);
        Assertions.assertThrows(ScriptEvaluationError.class, lexer::scanTokens);
    }

    enum QuadrupleToken implements EnumTokenType {
        // --- could still be parsed as -- and -
        QUADRUPLE_DASH(DefaultTokenCharacter.MINUS),
        // No token for @, @@, or @@@, so must match QUADRUPLE_AT to be valid.
        QUADRUPLE_AT(SimpleTokenCharacter.of('@', true)),
        ;

        private final TokenCharacter character;

        QuadrupleToken(TokenCharacter character) {
            this.character = character;
        }

        @Override
        public TokenType delegate() {
            return TokenMetaData.builder(this)
                    .combines(character, character, character, character)
                    .ok();
        }
    }
}
