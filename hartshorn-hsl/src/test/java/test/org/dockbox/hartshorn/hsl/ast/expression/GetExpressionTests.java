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

import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.interpreter.VariableScope;
import org.dockbox.hartshorn.hsl.objects.ExternalObjectReference;
import org.dockbox.hartshorn.hsl.objects.InstanceReference;
import org.dockbox.hartshorn.hsl.objects.PropertyContainer;
import org.dockbox.hartshorn.hsl.objects.external.ExternalFunction;
import org.dockbox.hartshorn.hsl.parser.expression.CallExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.IdentifierExpressionParser;
import org.dockbox.hartshorn.hsl.token.Token;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import test.org.dockbox.hartshorn.hsl.HSLTestHelper;

public class GetExpressionTests {

    @Test
    void getExpressionReturnsPropertyContainerValue() {
        HSLTestHelper.ExpressionTestHelper helper = HSLTestHelper.ofExpression("object.value")
                .expressionParser(new CallExpressionParser())
                .expressionParser(new IdentifierExpressionParser());

        PropertyContainer container = Mockito.mock(PropertyContainer.class);
        Mockito.when(container.get(
                Mockito.any(Interpreter.class),
                Mockito.any(Token.class),
                Mockito.any(VariableScope.class)
        )).thenReturn("Hello, World!");
        helper.defineVariable("object", container);

        Object value = helper.interpretValue();
        Assertions.assertEquals("Hello, World!", value);
    }

    @Test
    void getExpressionWithExternalObjectReferenceReturnsExternalObject() {
        HSLTestHelper.ExpressionTestHelper helper = HSLTestHelper.ofExpression("object.value")
                .expressionParser(new CallExpressionParser())
                .expressionParser(new IdentifierExpressionParser());

        PropertyContainer container = Mockito.mock(PropertyContainer.class);
        Mockito.when(container.get(
                Mockito.any(Interpreter.class),
                Mockito.any(Token.class),
                Mockito.any(VariableScope.class)
        )).thenAnswer(invocation -> {
            ExternalObjectReference objectReference = Mockito.mock(ExternalObjectReference.class);
            Mockito.when(objectReference.externalObject()).thenReturn("Hello, World!");
            return objectReference;
        });
        helper.defineVariable("object", container);

        Object value = helper.interpretValue();
        // String, not ExternalObjectReference. Should unwrap automatically.
        Assertions.assertInstanceOf(String.class, value);
        Assertions.assertEquals("Hello, World!", value);
    }

    @Test
    void getExpressionWithInstanceReferenceAndExternalFunctionShouldReturnBoundFunction() {
        HSLTestHelper.ExpressionTestHelper helper = HSLTestHelper.ofExpression("object.value")
                .expressionParser(new CallExpressionParser())
                .expressionParser(new IdentifierExpressionParser());

        InstanceReference instanceReference = Mockito.mock(InstanceReference.class);
        Mockito.when(instanceReference.get(
                Mockito.eq(helper.interpreter()),
                Mockito.any(Token.class),
                Mockito.any(VariableScope.class)
        )).thenAnswer(invocation -> {
            ExternalFunction function = Mockito.mock(ExternalFunction.class);
            Mockito.when(function.bind(Mockito.any(InstanceReference.class)))
                    .thenAnswer(functionInvocation -> {
                        InstanceReference argument = functionInvocation
                                .getArgument(0, InstanceReference.class);
                        Mockito.when(function.bound()).thenReturn(argument);
                        return function;
                    });
            return function;
        });
        helper.defineVariable("object", instanceReference);

        Object value = helper.interpretValue();
        ExternalFunction externalFunction = Assertions.assertInstanceOf(ExternalFunction.class, value);
        InstanceReference bound = externalFunction.bound();
        Assertions.assertSame(instanceReference, bound);
    }
}