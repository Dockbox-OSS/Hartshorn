/*
 * Copyright 2019-2026 the original author or authors.
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

import org.assertj.core.api.InstanceOfAssertFactories;
import org.dockbox.hartshorn.hsl.interpreter.Array;
import org.dockbox.hartshorn.hsl.parser.expression.IdentifierExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.LiteralExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.RangeExpressionParser;
import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.junit.jupiter.api.Test;
import test.org.dockbox.hartshorn.hsl.support.HSLTestHelper;

import static org.assertj.core.api.Assertions.assertThat;

@HartshornIntegrationTest(includeBasePackages = false)
class RangeExpressionTests {

    @Test
    void rangeYieldsLeftRightInclusiveArray(@Inject ApplicationContext applicationContext) {
        HSLTestHelper helper = HSLTestHelper.ofExpression(applicationContext, "1..5")
            .expressionParser(new RangeExpressionParser())
            .expressionParser(new LiteralExpressionParser())
            .build();

        Object value = helper.interpretValue();
        Array array = assertThat(value)
                .asInstanceOf(InstanceOfAssertFactories.type(Array.class))
                .actual();
        assertThat(array.values()).containsExactly(1d, 2d, 3d, 4d, 5d);
    }

    @Test
    void rangeWithSameValueYieldsSingleElementArray(@Inject ApplicationContext applicationContext) {
        HSLTestHelper helper = HSLTestHelper.ofExpression(applicationContext, "2..2")
            .expressionParser(new RangeExpressionParser())
            .expressionParser(new LiteralExpressionParser())
            .build();

        Object value = helper.interpretValue();
        Array array = assertThat(value)
                .asInstanceOf(InstanceOfAssertFactories.type(Array.class))
                .actual();
        assertThat(array.values()).containsExactly(2d);
    }

    @Test
    void rangeSupportsIdentifierValues(@Inject ApplicationContext applicationContext) {
        HSLTestHelper helper = HSLTestHelper.ofExpression(applicationContext, "min..max")
            .expressionParser(new RangeExpressionParser())
            .expressionParser(new IdentifierExpressionParser())
            .expressionParser(new LiteralExpressionParser())
            .defineLocal("min", 1)
            .defineLocal("max", 5)
            .build();

        Object value = helper.interpretValue();
        Array array = assertThat(value)
                .asInstanceOf(InstanceOfAssertFactories.type(Array.class))
                .actual();
        assertThat(array.values()).containsExactly(1d, 2d, 3d, 4d, 5d);
    }
}