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

import org.assertj.core.api.InstanceOfAssertFactories;
import org.dockbox.hartshorn.hsl.interpreter.Array;
import org.dockbox.hartshorn.hsl.parser.expression.ComplexArrayExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.LiteralExpressionParser;
import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.junit.jupiter.api.Test;
import test.org.dockbox.hartshorn.hsl.support.HSLTestHelper;

import static org.assertj.core.api.Assertions.assertThat;

@HartshornIntegrationTest(includeBasePackages = false)
class ArrayLiteralExpressionTests {

    @Test
    void emptyArrayLiteralYieldsEmptyArrayObject(
        @Inject ApplicationContext applicationContext
    ) {
        HSLTestHelper helper = HSLTestHelper.ofExpression(applicationContext, "[]")
            .expressionParser(new ComplexArrayExpressionParser())
            .build();
        Object value = helper.interpretValue();

        Array array = assertThat(value)
                .asInstanceOf(InstanceOfAssertFactories.type(Array.class))
                .actual();
        assertThat(array.length()).isZero();
    }

    @Test
    void singleValueArrayLiteralYieldsArrayObject(
        @Inject ApplicationContext applicationContext
    ) {
        HSLTestHelper helper = HSLTestHelper.ofExpression(applicationContext, "[\"test\"]")
            .expressionParser(new LiteralExpressionParser())
            .expressionParser(new ComplexArrayExpressionParser())
            .build();
        Object value = helper.interpretValue();

        Array array = assertThat(value)
                .asInstanceOf(InstanceOfAssertFactories.type(Array.class))
                .actual();
        assertThat(array.length()).isOne();
        assertThat(array.value(0)).isEqualTo("test");
    }

    @Test
    void multipleValueArrayLiteralYieldsArrayObject(
        @Inject ApplicationContext applicationContext
    ) {
        HSLTestHelper helper = HSLTestHelper.ofExpression(applicationContext, """
                ["test", "test2"]
                """)
            .expressionParser(new LiteralExpressionParser())
            .expressionParser(new ComplexArrayExpressionParser())
            .build();
        Object value = helper.interpretValue();

        Array array = assertThat(value)
                .asInstanceOf(InstanceOfAssertFactories.type(Array.class))
                .actual();
        assertThat(array.length()).isEqualTo(2);
        assertThat(array.value(0)).isEqualTo("test");
        assertThat(array.value(1)).isEqualTo("test2");
    }
}
