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
import org.dockbox.hartshorn.hsl.customizer.CodeCustomizer;
import org.dockbox.hartshorn.hsl.interpreter.VariableScope;
import org.dockbox.hartshorn.hsl.objects.ClassReference;
import org.dockbox.hartshorn.hsl.objects.InstanceReference;
import org.dockbox.hartshorn.hsl.objects.MethodReference;
import org.dockbox.hartshorn.hsl.parser.expression.LiteralExpressionParser;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.semantic.ClassType;
import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import test.org.dockbox.hartshorn.hsl.support.HSLTestHelper;

@HartshornIntegrationTest(includeBasePackages = false)
public class SuperExpressionTests {

    @Test
    void superMethodCanBeAccessedWithCurrentInstance(
        @Inject ApplicationContext applicationContext
    ) {
        // 'super.hello' rather than 'super.hello()', as we want to test resolution, not
        // invocation (which would be CallExpressionParser -> FunctionCallExpression).
        HSLTestHelper helper = HSLTestHelper.ofExpression(applicationContext, "super.hello")
            .expressionParser(new LiteralExpressionParser())
            .customize(CodeCustomizer.of(Phase.SEMANTIC_ANALYSIS, context -> {
                // To access 'super', we need to be in a subclass context
                context.resolver().currentClassType(ClassType.SUBCLASS);
            }))
            .build();

        Expression expression = helper.expression();
        // Resolve scope distance for 'super' to 1 (one level up, to parent)
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

        // Define 'super' in the parent scope (parent class)
        VariableScope parentScope = helper.interpreter().visitingScope();
        parentScope.define("super", classReference);

        // Define 'this' in a sub-scope (child class)
        InstanceReference instanceReference = Mockito.mock(InstanceReference.class);
        VariableScope subScope = new VariableScope(parentScope);
        subScope.define("this", instanceReference);
        helper.interpreter().enterScope(subScope);

        Object value = helper.interpretOnly()
            .captures()
            .capturedValue();
        Assertions.assertSame(methodReference, value);
        Assertions.assertSame(instanceReference, methodReference.bound());
    }
}