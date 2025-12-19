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
import org.dockbox.hartshorn.hsl.parser.expression.UnaryExpressionParser;
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
public class UnaryExpressionTests {

    @Inject
    private ApplicationContext applicationContext;

    @ParameterizedTest(name = "{0} {1} = {2}")
    @MethodSource("logicalCases")
    void verifyUnaryExpression(
        String operator,
        Object right,
        Object expected
    ) {
        HSLTestHelper helper = HSLTestHelper.ofExpression(
                this.applicationContext,
                "%sright".formatted(operator)
            )
            .parser(parsers -> parsers
                .expressionParser(new UnaryExpressionParser())
                .expressionParser(new IdentifierExpressionParser())
            )
            .defineLocal("right", right)
            .build();

        Object value = helper.interpretValue();
        Assertions.assertEquals(expected, value);
    }

    public static Stream<Arguments> logicalCases() {
        return Stream.of(
            // BANG
            unary("!", true, false),
            unary("!", false, true),

            // MINUS
            unary("-", 5, -5d),
            unary("-", -10, 10d),
            unary("-", 0, -0d),

            // PLUS_PLUS
            unary("++", 5, 6d),
            unary("++", -1, 0d),
            unary("++", 0, 1d)

            // MINUS_MINUS
            , unary("--", 5, 4d),
            unary("--", -1, -2d),
            unary("--", 0, -1d),

            // COMPLEMENT
            unary("~", 5, -6d),
            unary("~", -1, 0d),
            unary("~", 0, -1d)
        );
    }

    public static Arguments unary(
        String operator,
        Object right,
        Object expected
    ) {
        return Arguments.of(operator, right, expected);
    }
}