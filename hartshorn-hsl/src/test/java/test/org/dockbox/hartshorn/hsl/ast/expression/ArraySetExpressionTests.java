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

import org.dockbox.hartshorn.hsl.ast.expression.ArraySetExpression;
import org.dockbox.hartshorn.hsl.interpreter.Array;
import org.dockbox.hartshorn.hsl.interpreter.expression.ArraySetExpressionInterpreter;
import org.dockbox.hartshorn.hsl.parser.expression.AssignExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.IdentifierExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.LiteralExpressionParser;
import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.junit.jupiter.api.Test;
import test.org.dockbox.hartshorn.hsl.support.HSLTestHelper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

@HartshornIntegrationTest(includeBasePackages = false)
class ArraySetExpressionTests {

    @Test
    void setWithinArrayRange(@Inject ApplicationContext applicationContext) {
        Object[] realArray = {"test"};
        Array hslArray = new Array(realArray);
        HSLTestHelper helper = HSLTestHelper.ofExpression(applicationContext, """
                array[0] = "value"
                """)
            .expressionParser(new AssignExpressionParser())
            .expressionParser(new LiteralExpressionParser())
            .expressionParser(new IdentifierExpressionParser())
            .defineLocal("array", hslArray)
            .build();

        Object interpreted = helper
            .evaluateWith(ArraySetExpression.class, new ArraySetExpressionInterpreter())
            .interpretValue();
        assertThat(interpreted).isEqualTo("value");
        assertThat(hslArray.value(0)).isEqualTo("value");
        assertThat(realArray[0]).isEqualTo("value");
    }

    @Test
    void setOutsideRangeThrowsOutOfBounds(@Inject ApplicationContext applicationContext) {
        Object[] realArray = {"test"};
        Array hslArray = new Array(realArray);
        HSLTestHelper helper = HSLTestHelper.ofExpression(applicationContext, """
                array[1] = "value"
                """)
            .expressionParser(new AssignExpressionParser())
            .expressionParser(new LiteralExpressionParser())
            .expressionParser(new IdentifierExpressionParser())
            .defineLocal("array", hslArray)
            .build();

        assertThatExceptionOfType(IndexOutOfBoundsException.class).isThrownBy(() ->
            helper.evaluateWith(
                ArraySetExpression.class,
                new ArraySetExpressionInterpreter()
            ).interpretValue());
    }
}