/*
 * Copyright 2019-2023 the original author or authors.
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

package test.org.dockbox.hartshorn.conditions;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.condition.ActivatorCondition;
import org.dockbox.hartshorn.component.condition.ClassCondition;
import org.dockbox.hartshorn.component.condition.Condition;
import org.dockbox.hartshorn.component.condition.ConditionContext;
import org.dockbox.hartshorn.component.condition.ConditionResult;
import org.dockbox.hartshorn.component.condition.RequiresActivator;
import org.dockbox.hartshorn.component.condition.RequiresClass;
import org.dockbox.hartshorn.component.condition.RequiresCondition;
import org.dockbox.hartshorn.inject.binding.BindingHierarchy;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.testsuite.ModifyApplication;
import org.dockbox.hartshorn.testsuite.TestComponents;
import org.dockbox.hartshorn.testsuite.TestCustomizer;
import org.dockbox.hartshorn.testsuite.TestProperties;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import jakarta.inject.Inject;

@HartshornTest(includeBasePackages = false)
@TestComponents(components = ConditionalProviders.class)
@TestProperties({
        "--property.c=o",
        "--property.d=d",
        "--property.e=otherValue"
})
public class ConditionTests {

    @Inject
    private ApplicationContext applicationContext;

    @ModifyApplication
    public static void factory() {
        TestCustomizer.BUILDER.compose(builder -> {
            builder.arguments(List.of(
                    "--property.c=o",
                    "--property.d=d",
                    "--property.e=otherValue"
            ));
        });
        TestCustomizer.CONSTRUCTOR.compose(constructor -> {
            constructor.activators(activators -> {
                activators.add(TypeUtils.annotation(DemoActivator.class));
            });
        });
    }

    public static Stream<Arguments> properties() {
        return Stream.of(
                Arguments.of("a", true),
                Arguments.of("b", false),
                Arguments.of("c", true),
                Arguments.of("d", true),
                Arguments.of("e", false),
                Arguments.of("f", true)
        );
    }

    @ParameterizedTest
    @MethodSource("properties")
    @TestComponents(components = ConditionalProviders.class)
    void testPropertyConditions(final String name, final boolean present) {
        final ComponentKey<String> key = ComponentKey.builder(String.class).name(name).build();
        final BindingHierarchy<String> hierarchy = this.applicationContext.hierarchy(key);
        Assertions.assertEquals(present ? 1 : 0, hierarchy.size());

        final String value = this.applicationContext.get(key);
        if (present) {
            Assertions.assertEquals(name, value);
        }
        else {
            Assertions.assertEquals("", value); // Default value, not null
        }
    }

    @Test
    void testActivatorConditions() {
        Assertions.assertTrue(this.applicationContext.hasActivator(DemoActivator.class));

        final MethodView<ConditionTests, ?> method = this.applicationContext.environment()
                .introspect(ConditionTests.class)
                .methods()
                .named("requiresActivator")
                .get();
        final RequiresCondition annotation = method.annotations().get(RequiresCondition.class).get();

        final ConditionContext context = new ConditionContext(this.applicationContext, method, annotation);
        final Condition condition = new ActivatorCondition();

        final ConditionResult result = condition.matches(context);
        Assertions.assertTrue(result.matches());
    }

    @RequiresActivator(DemoActivator.class)
    private void requiresActivator() {}

    @Test
    void testClassConditions() {
        final TypeView<ConditionTests> type = this.applicationContext.environment().introspect(ConditionTests.class);
        final Condition condition = new ClassCondition();

        final MethodView<ConditionTests, ?> requiresClass = type.methods().named("requiresClass").get();
        final RequiresCondition annotationForPresent = requiresClass.annotations().get(RequiresCondition.class).get();
        final ConditionContext contextForPresent = new ConditionContext(this.applicationContext, requiresClass, annotationForPresent);
        Assertions.assertTrue(condition.matches(contextForPresent).matches());

        final MethodView<ConditionTests, ?> requiresAbsentClass = type.methods().named("requiresAbsentClass").get();
        final RequiresCondition annotationForAbsent = requiresAbsentClass.annotations().get(RequiresCondition.class).get();
        final ConditionContext contextForAbsent = new ConditionContext(this.applicationContext, requiresAbsentClass, annotationForAbsent);
        Assertions.assertFalse(condition.matches(contextForAbsent).matches());
    }
    @RequiresClass("java.lang.String")
    private void requiresClass() {}

    @RequiresClass("java.gnal.String")
    private void requiresAbsentClass() {}

}
