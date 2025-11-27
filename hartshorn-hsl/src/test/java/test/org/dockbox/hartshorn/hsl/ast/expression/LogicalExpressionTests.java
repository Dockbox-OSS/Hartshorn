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

import org.dockbox.hartshorn.hsl.parser.expression.IdentifierExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.LogicalExpressionParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import test.org.dockbox.hartshorn.hsl.HSLTestHelper;

import java.util.stream.Stream;

public class LogicalExpressionTests {

    @ParameterizedTest(name = "{0} {1} {2} = {3}")
    @MethodSource("logicalCases")
    void verifyLogicalExpression(
            Object left,
            String operator,
            Object right,
            Object expected
    ) {
        HSLTestHelper.ExpressionTestHelper helper = HSLTestHelper.ofExpression(
                        "left %s right".formatted(operator)
                )
                .defineVariable("left", left)
                .defineVariable("right", right)
                .expressionParser(new LogicalExpressionParser())
                .expressionParser(new IdentifierExpressionParser());

        Object value = helper.interpretValue();
        Assertions.assertEquals(expected, value);
    }

    public static Stream<Arguments> logicalCases() {
        return Stream.of(
                // XOR
                logical(true, "^", true, false),
                logical(true, "^", false, true),
                logical(false, "^", true, true),
                logical(false, "^", false, false),

                // OR
                logical(true, "||", true, true),
                logical(true, "||", false, true),
                logical(false, "||", true, true),
                logical(false, "||", false, false),

                // AND
                logical(true, "&&", true, true),
                logical(true, "&&", false, false),
                logical(false, "&&", true, false),
                logical(false, "&&", false, false)
        );
    }

    public static Arguments logical(
            Object left,
            String operator,
            Object right,
            Object expected
    ) {
        return Arguments.of(left, operator, right, expected);
    }
}