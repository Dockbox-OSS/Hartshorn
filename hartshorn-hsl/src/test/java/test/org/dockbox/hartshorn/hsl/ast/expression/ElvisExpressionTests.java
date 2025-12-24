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

import org.dockbox.hartshorn.hsl.parser.expression.ElvisExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.LiteralExpressionParser;
import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.junit.jupiter.api.Test;
import test.org.dockbox.hartshorn.hsl.support.HSLTestHelper;

import static org.assertj.core.api.Assertions.assertThat;

@HartshornIntegrationTest(includeBasePackages = false)
class ElvisExpressionTests {

    @Test
    void elvisWithTruthyValueReturnsLeft(@Inject ApplicationContext applicationContext) {
        HSLTestHelper helper = HSLTestHelper.ofExpression(applicationContext, "42 ?: 24")
            .expressionParser(new ElvisExpressionParser())
            .expressionParser(new LiteralExpressionParser())
            .build();

        Object value = helper.interpretValue();
        assertThat(value).isEqualTo(42d);
    }

    @Test
    void elvisWithFalsyValueReturnsRight(@Inject ApplicationContext applicationContext) {
        HSLTestHelper helper = HSLTestHelper.ofExpression(applicationContext, "null ?: 24")
            .expressionParser(new ElvisExpressionParser())
            .expressionParser(new LiteralExpressionParser())
            .build();

        Object value = helper.interpretValue();
        assertThat(value).isEqualTo(24d);
    }
}