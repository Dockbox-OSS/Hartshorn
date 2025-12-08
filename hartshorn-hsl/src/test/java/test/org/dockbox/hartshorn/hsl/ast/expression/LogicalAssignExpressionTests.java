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

package test.org.dockbox.hartshorn.hsl.ast.expression;

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.parser.expression.IdentifierExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.LiteralExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.LogicalAssignExpressionParser;
import org.dockbox.hartshorn.hsl.runtime.DiagnosticMessage;
import org.dockbox.hartshorn.hsl.token.DefaultTokenRegistry;
import org.dockbox.hartshorn.hsl.token.type.BitwiseAssignmentTokenType;
import org.dockbox.hartshorn.hsl.token.type.TokenType;
import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import test.org.dockbox.hartshorn.hsl.support.HSLTestHelper;
import test.org.dockbox.hartshorn.hsl.support.ScriptAssertions;

import java.util.List;
import java.util.stream.Stream;

@HartshornIntegrationTest(includeBasePackages = false)
public class LogicalAssignExpressionTests {

    @Inject
    private ApplicationContext applicationContext;

    public static Stream<Arguments> logicalTokenTypes() {
        return Stream.of(
                Arguments.of(BitwiseAssignmentTokenType.SHIFT_RIGHT_EQUAL, 0b10), // 2
                Arguments.of(BitwiseAssignmentTokenType.SHIFT_LEFT_EQUAL, 0b1010), // 10
                Arguments.of(BitwiseAssignmentTokenType.LOGICAL_SHIFT_RIGHT_EQUAL, 0b10), // 2
                Arguments.of(BitwiseAssignmentTokenType.BITWISE_AND_EQUAL, 0b1), // 1
                Arguments.of(BitwiseAssignmentTokenType.BITWISE_OR_EQUAL, 0b101), // 5
                Arguments.of(BitwiseAssignmentTokenType.XOR_EQUAL, 0b100), // 4
                Arguments.of(BitwiseAssignmentTokenType.COMPLEMENT_EQUAL, -0b110) // -6
        );
    }

    @Test
    void verifyCasesCovered() {
        List<TokenType> coveredTokenTypes = logicalTokenTypes()
                .map(Arguments::get)
                .map(args -> (TokenType) args[0])
                .toList();
        DefaultTokenRegistry.createDefault()
                .tokenTypes(token -> token.assignsWith() != null)
                .forEach(tokenType -> {
                    Assertions.assertTrue(coveredTokenTypes.contains(tokenType),
                            "Token type %s is not covered by tests".formatted(
                                    tokenType.representation()
                            ));
                });
    }

    @ParameterizedTest
    @MethodSource("logicalTokenTypes")
    void logicalAssignUpdatesVariableAndReturnsNewValue(TokenType tokenType, int result) {
        HSLTestHelper helper = HSLTestHelper.ofExpression(
                this.applicationContext,
                        "a %s 1".formatted(tokenType.representation())
                )
                .parser(parser -> {
                    parser.expressionParser(new LogicalAssignExpressionParser(parser.tokenRegistry()));
                })
                .expressionParser(new IdentifierExpressionParser())
                .expressionParser(new LiteralExpressionParser())
                .defineLocal("a", 0b101)
                .build();

        Object value = helper.interpretValue();
        Assertions.assertEquals(result, value);
        Assertions.assertEquals(result, helper.findVariable("a"));
    }

    @ParameterizedTest
    @MethodSource("logicalTokenTypes")
    void logicalAssignCanNotAssignToNonVariable(TokenType tokenType) {
        HSLTestHelper helper = HSLTestHelper.ofExpression(
                this.applicationContext,
                        "1 %s 1".formatted(tokenType.representation())
                )
                .parser(parser -> {
                    parser.expressionParser(new LogicalAssignExpressionParser(parser.tokenRegistry()));
                })
                .expressionParser(new LiteralExpressionParser())
                .build();

        ScriptEvaluationError error = Assertions.assertThrows(
                ScriptEvaluationError.class,
                helper::expression
        );
        ScriptAssertions.assertEvaluationError(error, DiagnosticMessage.INVALID_ASSIGNMENT_TARGET);
    }
}