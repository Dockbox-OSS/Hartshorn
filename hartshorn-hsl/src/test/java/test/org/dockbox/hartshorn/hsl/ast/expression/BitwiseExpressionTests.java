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

import org.dockbox.hartshorn.hsl.parser.expression.BitwiseExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.IdentifierExpressionParser;
import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import test.org.dockbox.hartshorn.hsl.support.HSLTestHelper;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@HartshornIntegrationTest(includeBasePackages = false)
public class BitwiseExpressionTests {

    @Inject
    private ApplicationContext applicationContext;

    @ParameterizedTest(name = "{0} {1} {2} = {3}")
    @MethodSource("bitwiseCases")
    void verifyBitwiseExpression(
        Object left,
        String operator,
        Object right,
        Object expected
    ) {
        HSLTestHelper helper = HSLTestHelper.ofExpression(
                this.applicationContext,
                "left %s right".formatted(operator)
            )
            .defineLocal("left", left)
            .defineLocal("right", right)
            .expressionParser(new BitwiseExpressionParser())
            .expressionParser(new IdentifierExpressionParser())
            .build();

        Object value = helper.interpretValue();
        assertThat(value).isEqualTo(expected);
    }

    public static Stream<Arguments> bitwiseCases() {
        return Stream.of(
            // Shift left
            bitwise(0b0001, "<<", 2, 0b0100),
            bitwise(0b0011, "<<", 1, 0b0110),

            // Shift right
            bitwise(0b0100, ">>", 2, 0b0001),
            bitwise(0b0110, ">>", 1, 0b0011),

            // Logical shift right
            bitwise(0b0100, ">>>", 2, 0b0001),
            bitwise(0b0110, ">>>", 1, 0b0011),

            // Bitwise OR
            bitwise(0b0101, "|", 0b0011, 0b0111),
            bitwise(0b0000, "|", 0b0000, 0b0000),
            bitwise(0b1111, "|", 0b0000, 0b1111),

            // Bitwise AND
            bitwise(0b0101, "&", 0b0011, 0b0001),
            bitwise(0b1111, "&", 0b0000, 0b0000),
            bitwise(0b1111, "&", 0b1111, 0b1111)
        );
    }

    public static Arguments bitwise(
        Object left,
        String operator,
        Object right,
        Object expected
    ) {
        return Arguments.of(left, operator, right, expected);
    }
}