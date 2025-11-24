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

import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.interpreter.VariableScope;
import org.dockbox.hartshorn.hsl.objects.ClassReference;
import org.dockbox.hartshorn.hsl.objects.InstanceReference;
import org.dockbox.hartshorn.hsl.objects.MethodReference;
import org.dockbox.hartshorn.hsl.parser.expression.LiteralExpressionParser;
import org.dockbox.hartshorn.hsl.semantic.Resolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import test.org.dockbox.hartshorn.hsl.ast.HSLTestHelper;

public class SuperExpressionInterpreterTests {

    @Test
    void superMethodCanBeAccessedWithCurrentInstance() {
        HSLTestHelper.ExpressionTestHelper helper = HSLTestHelper.ofExpression("super.hello")
                .expressionParser(new LiteralExpressionParser());

        helper.resolver().currentClass(Resolver.ClassType.SUBCLASS);
        Expression expression = helper.parseExpression();

        helper.interpreter().state().resolve(expression, 1);

        ClassReference classReference = Mockito.mock(ClassReference.class);
        MethodReference methodReference = Mockito.mock(MethodReference.class);
        Mockito.when(classReference.method("hello"))
                .thenReturn(methodReference);
        Mockito.when(methodReference.bind(Mockito.any(InstanceReference.class)))
                .thenAnswer(invocation -> {
                    InstanceReference argument = invocation.getArgument(0, InstanceReference.class);
                    Mockito.when(methodReference.bound()).thenReturn(argument);
                    return methodReference;
                });

        VariableScope parentScope = helper.interpreter().visitingScope();
        parentScope.define("super", classReference);

        InstanceReference instanceReference = Mockito.mock(InstanceReference.class);
        VariableScope subScope = new VariableScope(parentScope);
        subScope.define("this", instanceReference);
        helper.interpreter().enterScope(subScope);

        Object value = helper.interpretValue();
        Assertions.assertSame(methodReference, value);
        Assertions.assertSame(instanceReference, methodReference.bound());
    }
}