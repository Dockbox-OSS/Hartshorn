/*
 * Copyright 2019-2022 the original author or authors.
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

package org.dockbox.hartshorn.hsl;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.condition.ConditionContext;
import org.dockbox.hartshorn.component.condition.ConditionResult;
import org.dockbox.hartshorn.context.Context;
import org.dockbox.hartshorn.hsl.condition.ExpressionCondition;
import org.dockbox.hartshorn.hsl.condition.ExpressionConditionContext;
import org.dockbox.hartshorn.hsl.condition.RequiresExpression;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.util.reflect.AnnotatedElementContext;
import org.dockbox.hartshorn.util.reflect.AnnotatedElementModifier;
import org.dockbox.hartshorn.util.reflect.MethodContext;
import org.dockbox.hartshorn.util.reflect.TypeContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.stream.Stream;

import jakarta.inject.Inject;

@HartshornTest
@UseExpressionValidation
public class ConditionTests {

    @Inject
    private ApplicationContext applicationContext;

    public static Stream<Arguments> scripts() {
        return Stream.of(
                Arguments.of("1==1", true), // Yield true
                Arguments.of("1==2", false), // Yield false
                Arguments.of("1==a", false), // Undefined variable, runtime error
                Arguments.of("1@=1", false), // Tokenizing error
                Arguments.of("1++=1", false) // Parsing error
        );
    }

    @ParameterizedTest
    @MethodSource("scripts")
    void testBulkValidations(final String expression, final boolean matches) {
        final ConditionResult result = match(expression);
        Assertions.assertEquals(matches, result.matches());
    }

    @Test
    void testApplicationContextIsOptInAvailable() {
        final ExpressionConditionContext context = new ExpressionConditionContext().includeApplicationContext(true);
        final String expression = "applicationContext.getClass().getName() == \"%s\"".formatted(applicationContext.getClass().getName());
        final ConditionResult result = match(expression, context);
        Assertions.assertTrue(result.matches());
    }

    @Test
    void testApplicationContextIsNotAvailableDefault() {
        final String expression = "applicationContext != null";
        final ConditionResult result = match(expression);
        Assertions.assertFalse(result.matches());

        final String message = result.message();
        final String withoutMarker = message.substring(0, message.indexOf('.'));
        // Ensure the failure is due to the absence of the application context, not due to it being null
        Assertions.assertEquals("Undefined variable 'applicationContext'", withoutMarker);
    }

    ConditionResult match(final String expression, final Context... contexts) {
        final ExpressionCondition condition = this.applicationContext.get(ExpressionCondition.class);
        final AnnotatedElementContext<?> element = enhanceWithVirtualCondition(expression);
        final ConditionContext context = new ConditionContext(this.applicationContext, element, null);
        for (final Context child : contexts) context.add(child);
        return condition.matches(context);
    }

    private AnnotatedElementContext<?> enhanceWithVirtualCondition(final String expression) {
        final MethodContext<?, ?> method = TypeContext.of(this).method("mockTarget").get();
        final RequiresExpression annotation = new RequiresExpression() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return RequiresExpression.class;
            }

            @Override
            public String value() {
                return expression;
            }
        };
        final AnnotatedElementModifier<Method> modifier = AnnotatedElementModifier.of(method);
        modifier.add(annotation);
        return method;
    }

    private void mockTarget() {}
}
