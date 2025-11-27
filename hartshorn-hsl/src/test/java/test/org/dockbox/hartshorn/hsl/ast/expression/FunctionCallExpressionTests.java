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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import test.org.dockbox.hartshorn.hsl.HSLTestHelper;

public class FunctionCallExpressionTests {

    @Test
    void functionWithoutArgumentsCanBeCalled() {
        HSLTestHelper.ExpressionTestHelper helper = HSLTestHelper.ofExpression("sayHello()")
                .expressionParser(new CallExpressionParser())
                .expressionParser(new IdentifierExpressionParser());

        CallableNode node = (at, interpreter, instance, args) -> {
            Assertions.assertNull(instance);
            Assertions.assertTrue(args.isEmpty());
            return "Hello world!";
        };
        helper.defineVariable("sayHello", node);

        Object value = helper.interpretValue();
        Assertions.assertEquals("Hello world!", value);
    }

    @Test
    void functionWithArgumentsCanBeCalled() {
        HSLTestHelper.ExpressionTestHelper helper = HSLTestHelper.ofExpression("""
                        greet("Guus")
                        """)
                .expressionParser(new CallExpressionParser())
                .expressionParser(new LiteralExpressionParser())
                .expressionParser(new IdentifierExpressionParser());

        CallableNode node = (at, interpreter, instance, args) -> {
            Assertions.assertNull(instance);
            Assertions.assertEquals(1, args.size());
            Assertions.assertEquals("Guus", args.getFirst());

            return "Hello " + args.getFirst() + "!";
        };
        helper.defineVariable("greet", node);

        Object value = helper.interpretValue();
        Assertions.assertEquals("Hello Guus!", value);
    }

    @Test
    void externalObjectReferenceArgumentIsUnwrapped() {
        HSLTestHelper.ExpressionTestHelper helper = HSLTestHelper.ofExpression("sayHello(person)")
                .expressionParser(new CallExpressionParser())
                .expressionParser(new IdentifierExpressionParser());

        ExternalObjectReference objectReference = Mockito.mock(ExternalObjectReference.class);
        Mockito.when(objectReference.externalObject()).thenReturn("Guus");
        helper.defineVariable("person", objectReference);

        CallableNode node = (at, interpreter, instance, args) -> {
            Assertions.assertNull(instance);
            Assertions.assertEquals(1, args.size());
            // Thus, not an ExternalObjectReference anymore
            Assertions.assertEquals("Guus", args.getFirst());

            return "Hello world!";
        };
        helper.defineVariable("sayHello", node);

        Object value = helper.interpretValue();
        Assertions.assertEquals("Hello world!", value);
    }
}