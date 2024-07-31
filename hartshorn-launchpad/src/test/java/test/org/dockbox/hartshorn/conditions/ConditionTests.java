/*
 * Copyright 2019-2024 the original author or authors.
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

import java.util.List;
import java.util.stream.Stream;

import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.launchpad.condition.ActivatorCondition;
import org.dockbox.hartshorn.inject.condition.AnnotationConditionDeclaration;
import org.dockbox.hartshorn.inject.condition.support.ClassCondition;
import org.dockbox.hartshorn.inject.condition.Condition;
import org.dockbox.hartshorn.inject.condition.ConditionContext;
import org.dockbox.hartshorn.inject.condition.ConditionMatcher;
import org.dockbox.hartshorn.inject.condition.ConditionResult;
import org.dockbox.hartshorn.launchpad.condition.RequiresActivator;
import org.dockbox.hartshorn.inject.condition.support.RequiresClass;
import org.dockbox.hartshorn.inject.condition.RequiresCondition;
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

import org.dockbox.hartshorn.inject.annotations.Inject;

@SuppressWarnings("unused")
@HartshornTest(includeBasePackages = false)
@TestComponents(components = ConditionalConfiguration.class)
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
    @TestComponents(components = ConditionalConfiguration.class)
    void testPropertyConditions(String name, boolean present) {
        ComponentKey<String> key = ComponentKey.builder(String.class).name(name).build();
        BindingHierarchy<String> hierarchy = this.applicationContext.hierarchy(key);
        Assertions.assertEquals(present ? 1 : 0, hierarchy.size());

        String value = this.applicationContext.get(key);
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

        MethodView<ConditionTests, ?> method = this.applicationContext.environment()
                .introspector()
                .introspect(ConditionTests.class)
                .methods()
                .named("requiresActivator")
                .get();
        RequiresCondition annotation = method.annotations().get(RequiresCondition.class).get();
        AnnotationConditionDeclaration declaration = new AnnotationConditionDeclaration(annotation);
        ConditionContext context = new ConditionContext(this.applicationContext, method, declaration);
        Condition condition = new ActivatorCondition();

        ConditionResult result = condition.matches(context);
        Assertions.assertTrue(result.matches());
    }

    @RequiresActivator(DemoActivator.class)
    private void requiresActivator() {}

    @Test
    void testClassConditions() {
        TypeView<ConditionTests> type = this.applicationContext.environment().introspector().introspect(ConditionTests.class);
        Condition condition = new ClassCondition();

        MethodView<ConditionTests, ?> requiresClass = type.methods().named("requiresClass").get();
        RequiresCondition annotationForPresent = requiresClass.annotations().get(RequiresCondition.class).get();
        ConditionContext contextForPresent = new ConditionContext(this.applicationContext, requiresClass, new AnnotationConditionDeclaration(annotationForPresent));
        Assertions.assertTrue(condition.matches(contextForPresent).matches());

        MethodView<ConditionTests, ?> requiresAbsentClass = type.methods().named("requiresAbsentClass").get();
        RequiresCondition annotationForAbsent = requiresAbsentClass.annotations().get(RequiresCondition.class).get();
        ConditionContext contextForAbsent = new ConditionContext(this.applicationContext, requiresAbsentClass, new AnnotationConditionDeclaration(annotationForAbsent));
        Assertions.assertFalse(condition.matches(contextForAbsent).matches());
    }

    @RequiresClass("java.lang.String")
    private void requiresClass() {}

    @RequiresClass("java.gnal.String")
    private void requiresAbsentClass() {}

    @Test
    void testEnclosedViewsIncludeParentCondition() {
        TypeView<ParentClass> type = this.applicationContext.environment().introspector().introspect(ParentClass.class);
        ConditionMatcher matcher = new ConditionMatcher(this.applicationContext);
        Assertions.assertFalse(matcher.match(type));

        MethodView<ParentClass, ?> methodView = type.methods().named("requiresClass").get();
        matcher.includeEnclosingConditions(false);
        Assertions.assertTrue(matcher.match(methodView));

        matcher.includeEnclosingConditions(true);
        Assertions.assertFalse(matcher.match(methodView));
    }

    @RequiresClass("java.gnal.String")
    public static class ParentClass {
        @RequiresClass("java.lang.String")
        public void requiresClass() {}
    }
}
