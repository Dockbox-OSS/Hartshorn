/*
 * Copyright 2019-2025 the original author or authors.
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import test.org.dockbox.hartshorn.hsl.interpreter.InterpreterTestHelper;

public class LexerTests {

    public static Stream<Arguments> tokens() {
        final List<Arguments> arguments = new ArrayList<>();
        DefaultTokenRegistry tokenRegistry = DefaultTokenRegistry.createDefault();
        Set<TokenType> nonLiteralTokens = tokenRegistry.tokenTypes(type -> !(type instanceof LiteralTokenType || tokenRegistry.comments().resolveFromOpenToken(type).present()));
        for (TokenType type : nonLiteralTokens) {
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
    void correctToken(String text, TokenType expected) {
        Lexer lexer = new SimpleTokenRegistryLexer(text, InterpreterTestHelper.defaultTokenRegistry());
        List<Token> tokens = lexer.scanTokens();

        assertThat(tokens)
                .hasSize(2);

        Token token = tokens.getFirst();
        assertThat(token.type()).isEqualTo(expected);
        assertThat(token.line()).isOne();

        Token eof = tokens.get(1);
        assertThat(eof.type()).isEqualTo(LiteralTokenType.EOF);
    }

    @Test
    void singleLineComment() {
        Lexer lexer = new SimpleTokenRegistryLexer("# Comment", InterpreterTestHelper.defaultTokenRegistry());
        List<Token> tokens = lexer.scanTokens();

        assertThat(tokens)
                .hasSize(1);

        Token token = tokens.getFirst();
        assertThat(token.type()).isEqualTo(LiteralTokenType.EOF);

        List<Comment> comments = lexer.comments();
        assertThat(comments)
                .hasSize(1);

        Comment comment = comments.getFirst();
        // Comments are not trimmed, include whitespace
        assertThat(comment.text()).isEqualTo(" Comment");
    }

    @Test
    void combinedOperatorsAreParsedCorrectly() {
        // No such operator (logical shift left), so should be parsed as '1 << < 2' (1 shift left, less than 2).
        // While this isn't valid code for HSL, it's a good test to see if the lexer is working as expected.
        final Lexer lexer = new SimpleTokenRegistryLexer("1 <<< 2", InterpreterTestHelper.defaultTokenRegistry());
        List<Token> tokens = lexer.scanTokens();
        assertThat(tokens).size().isSameAs(5);
        assertThat(tokens.get(0).type()).isEqualTo(LiteralTokenType.NUMBER);
        assertThat(tokens.get(1).type()).isEqualTo(BitwiseTokenType.SHIFT_LEFT);
        assertThat(tokens.get(2).type()).isEqualTo(ConditionTokenType.LESS);
        assertThat(tokens.get(3).type()).isEqualTo(LiteralTokenType.NUMBER);
        assertThat(tokens.get(4).type()).isEqualTo(LiteralTokenType.EOF);
    }

    @Test
    void incompleteTokenStepsBackToParent() {
        DefaultTokenRegistry registry = DefaultTokenRegistry.createDefault();
        registry.addTokens(QuadrupleToken.QUADRUPLE_DASH);

        // No token for triple dash, and quadruple dash is incomplete, so should match back based on parent
        // in token graph (from most specific to least specific). This should result in two tokens, one for
        // the double dash (MINUS_MINUS), and one for the single dash (MINUS).
        final Lexer lexer = new SimpleTokenRegistryLexer("---", registry);
        List<Token> tokens = lexer.scanTokens();

        assertThat(tokens).size().isSameAs(3);
        assertThat(tokens.get(0).type()).isEqualTo(ArithmeticTokenType.MINUS_MINUS);
        assertThat(tokens.get(1).type()).isEqualTo(ArithmeticTokenType.MINUS);
        assertThat(tokens.get(2).type()).isEqualTo(LiteralTokenType.EOF);
    }

    @Test
    void incompleteInvalidTokenFails() {
        DefaultTokenRegistry registry = DefaultTokenRegistry.createDefault();
        registry.addTokens(QuadrupleToken.QUADRUPLE_AT);

        final Lexer lexer = new SimpleTokenRegistryLexer("@@@", registry);
        assertThatExceptionOfType(ScriptEvaluationError.class).isThrownBy(lexer::scanTokens);
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
                    .combines(this.character, this.character, this.character, this.character)
                    .build();
        }
    }
}
