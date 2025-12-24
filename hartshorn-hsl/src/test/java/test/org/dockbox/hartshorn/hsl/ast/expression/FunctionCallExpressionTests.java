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

import org.dockbox.hartshorn.hsl.objects.CallableNode;
import org.dockbox.hartshorn.hsl.objects.ExternalObjectReference;
import org.dockbox.hartshorn.hsl.parser.expression.CallExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.IdentifierExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.LiteralExpressionParser;
import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import test.org.dockbox.hartshorn.hsl.support.HSLTestHelper;

import static org.assertj.core.api.Assertions.assertThat;

@HartshornIntegrationTest(includeBasePackages = false)
class FunctionCallExpressionTests {

    @Test
    void functionWithoutArgumentsCanBeCalled(@Inject ApplicationContext applicationContext) {
        CallableNode node = (at, interpreter, instance, args) -> {
            assertThat(instance).isNull();
            assertThat(args).isEmpty();
            return "Hello world!";
        };

        HSLTestHelper helper = HSLTestHelper.ofExpression(applicationContext, "sayHello()")
            .expressionParser(new CallExpressionParser())
            .expressionParser(new IdentifierExpressionParser())
            .defineLocal("sayHello", node)
            .build();

        Object value = helper.interpretValue();
        assertThat(value).isEqualTo("Hello world!");
    }

    @Test
    void functionWithArgumentsCanBeCalled(@Inject ApplicationContext applicationContext) {
        CallableNode node = (at, interpreter, instance, args) -> {
            assertThat(instance).isNull();
            assertThat(args).hasSize(1);
            assertThat(args).first().isEqualTo("Guus");

            return "Hello " + args.getFirst() + "!";
        };

        HSLTestHelper helper = HSLTestHelper.ofExpression(applicationContext, """
                greet("Guus")
                """)
            .expressionParser(new CallExpressionParser())
            .expressionParser(new LiteralExpressionParser())
            .expressionParser(new IdentifierExpressionParser())
            .defineLocal("greet", node)
            .build();

        Object value = helper.interpretValue();
        assertThat(value).isEqualTo("Hello Guus!");
    }

    @Test
    void externalObjectReferenceArgumentIsUnwrapped(@Inject ApplicationContext applicationContext) {
        ExternalObjectReference objectReference = Mockito.mock(ExternalObjectReference.class);
        Mockito.when(objectReference.externalObject()).thenReturn("Guus");

        CallableNode node = (at, interpreter, instance, args) -> {
            assertThat(instance).isNull();
            assertThat(args).hasSize(1);
            // Thus, not an ExternalObjectReference anymore
            assertThat(args).first().isEqualTo("Guus");

            return "Hello world!";
        };

        HSLTestHelper helper = HSLTestHelper.ofExpression(applicationContext, "sayHello(person)")
            .expressionParser(new CallExpressionParser())
            .expressionParser(new IdentifierExpressionParser())
            .defineLocal("person", objectReference)
            .defineLocal("sayHello", node)
            .build();

        Object value = helper.interpretValue();
        assertThat(value).isEqualTo("Hello world!");
    }
}