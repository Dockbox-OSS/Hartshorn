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

import org.dockbox.hartshorn.hsl.parser.expression.AbstractBitwiseOrLogicalExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.BinaryAdditionExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.BinaryComparisonExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.BinaryEqualityExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.BinaryMultiplicationExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.IdentifierExpressionParser;
import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import test.org.dockbox.hartshorn.hsl.support.HSLTestHelper;

import java.util.stream.Stream;

@HartshornIntegrationTest(includeBasePackages = false)
public class BinaryExpressionTests {

    @Inject
    private ApplicationContext applicationContext;

    @ParameterizedTest(name = "{1} {2} {3} = {4}")
    @MethodSource("binaryCases")
    void verifyBinaryExpression(
            AbstractBitwiseOrLogicalExpressionParser parser,
            Object left,
            String operator,
            Object right,
            Object expected
    ) {
        HSLTestHelper helper = HSLTestHelper.ofExpression(
                        applicationContext,
                        "left %s right".formatted(operator)
                )
                .defineLocal("left", left)
                .defineLocal("right", right)
                .expressionParser(parser)
                .expressionParser(new IdentifierExpressionParser())
                .build();

        Object value = helper.interpretValue();
        Assertions.assertEquals(expected, value);
    }

    public static Stream<Arguments> binaryCases() {
        return Stream.of(
                // Addition
                binary(new BinaryAdditionExpressionParser(), 5, "+", 10, 15d),
                binary(new BinaryAdditionExpressionParser(), 'A', "+", 5, (double) 'A' + 5),
                binary(new BinaryAdditionExpressionParser(), 5, "+", 'A', (double) 5 + 'A'),
                binary(new BinaryAdditionExpressionParser(), 'A', "+", 'B', "AB"),
                binary(new BinaryAdditionExpressionParser(), "Hello, ", "+", "world!", "Hello, world!"),
                binary(new BinaryAdditionExpressionParser(), "Value: ", "+", 42, "Value: 42"),
                binary(new BinaryAdditionExpressionParser(), 42, "+", " is the answer.", "42 is the answer."),

                // Subtraction
                binary(new BinaryAdditionExpressionParser(), 5, "-", 3, 2d),

                // Greater than
                binary(new BinaryComparisonExpressionParser(), 5, ">", 3, true),
                binary(new BinaryComparisonExpressionParser(), 3, ">", 5, false),
                binary(new BinaryComparisonExpressionParser(), 5, ">", 5, false),

                // Greater than or equal to
                binary(new BinaryComparisonExpressionParser(), 6, ">=", 5, true),
                binary(new BinaryComparisonExpressionParser(), 5, ">=", 5, true),
                binary(new BinaryComparisonExpressionParser(), 4, ">=", 5, false),

                // Less than
                binary(new BinaryComparisonExpressionParser(), 3, "<", 5, true),
                binary(new BinaryComparisonExpressionParser(), 5, "<", 3, false),
                binary(new BinaryComparisonExpressionParser(), 3, "<", 3, false),

                // Less than or equal to
                binary(new BinaryComparisonExpressionParser(), 4, "<=", 5, true),
                binary(new BinaryComparisonExpressionParser(), 5, "<=", 5, true),
                binary(new BinaryComparisonExpressionParser(), 6, "<=", 5, false),

                // Equality
                binary(new BinaryEqualityExpressionParser(), null, "==", null, true),
                binary(new BinaryEqualityExpressionParser(), null, "==", 5, false),
                binary(new BinaryEqualityExpressionParser(), 5, "==", 5, true),
                binary(new BinaryEqualityExpressionParser(), 5, "==", 10, false),
                binary(new BinaryEqualityExpressionParser(), "test", "==", "test", true),
                binary(new BinaryEqualityExpressionParser(), "test", "==", "TEST", false),

                // Inequality
                binary(new BinaryEqualityExpressionParser(), null, "!=", null, false),
                binary(new BinaryEqualityExpressionParser(), null, "!=", 5, true),
                binary(new BinaryEqualityExpressionParser(), 5, "!=", 5, false),
                binary(new BinaryEqualityExpressionParser(), 5, "!=", 10, true),
                binary(new BinaryEqualityExpressionParser(), "test", "!=", "test", false),
                binary(new BinaryEqualityExpressionParser(), "test", "!=", "TEST", true),

                // Multiplication
                binary(new BinaryMultiplicationExpressionParser(), 5, "*", 10, 50d),

                // Division
                binary(new BinaryMultiplicationExpressionParser(), 20, "/", 4, 5d),

                // Modulus
                binary(new BinaryMultiplicationExpressionParser(), 20, "%", 6, 2d)
        );
    }

    public static Arguments binary(
            AbstractBitwiseOrLogicalExpressionParser parser,
            Object left,
            String operator,
            Object right,
            Object expected
    ) {
        return Arguments.of(parser, left, operator, right, expected);
    }
}