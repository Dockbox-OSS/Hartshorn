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

package test.org.dockbox.hartshorn.inject.conditions;

import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.inject.binding.BindingHierarchy;
import org.dockbox.hartshorn.inject.condition.AnnotationConditionDeclaration;
import org.dockbox.hartshorn.inject.condition.Condition;
import org.dockbox.hartshorn.inject.condition.ConditionContext;
import org.dockbox.hartshorn.inject.condition.ConditionMatcher;
import org.dockbox.hartshorn.inject.condition.ConditionResult;
import org.dockbox.hartshorn.inject.condition.RequiresCondition;
import org.dockbox.hartshorn.inject.condition.support.ClassCondition;
import org.dockbox.hartshorn.inject.condition.support.RequiresClass;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.launchpad.condition.ActivatorCondition;
import org.dockbox.hartshorn.launchpad.condition.RequiresActivator;
import org.dockbox.hartshorn.test.annotations.TestComponents;
import org.dockbox.hartshorn.test.annotations.TestProperties;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("unused")
@HartshornIntegrationTest(includeBasePackages = false)
@TestComponents(ConditionalConfiguration.class)
@TestProperties({
        "property.c=o",
        "property.d=d",
        "property.e=otherValue"
})
@DemoActivator
public class ConditionTests {

    @Inject
    private ApplicationContext applicationContext;

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
    @TestComponents(ConditionalConfiguration.class)
    void propertyConditions(String name, boolean present) {
        ComponentKey<String> key = ComponentKey.builder(String.class).name(name).build();
        BindingHierarchy<String> hierarchy = this.applicationContext.hierarchy(key);
        assertThat(hierarchy.size()).isEqualTo(present ? 1 : 0);

        String value = this.applicationContext.get(key);
        if (present) {
            assertThat(value).isEqualTo(name);
        }
        else {
            assertThat(value).isEmpty(); // Default value, not null
        }
    }

    @Test
    void activatorConditions() {
        assertThat(this.applicationContext.activators().hasActivator(DemoActivator.class)).isTrue();

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
        assertThat(result.matches()).isTrue();
    }

    @RequiresActivator(DemoActivator.class)
    private void requiresActivator() {}

    @Test
    void classConditions() {
        TypeView<ConditionTests> type = this.applicationContext.environment().introspector().introspect(ConditionTests.class);
        Condition condition = new ClassCondition();

        MethodView<ConditionTests, ?> requiresClass = type.methods().named("requiresClass").get();
        RequiresCondition annotationForPresent = requiresClass.annotations().get(RequiresCondition.class).get();
        ConditionContext contextForPresent = new ConditionContext(this.applicationContext, requiresClass, new AnnotationConditionDeclaration(annotationForPresent));
        assertThat(condition.matches(contextForPresent).matches()).isTrue();

        MethodView<ConditionTests, ?> requiresAbsentClass = type.methods().named("requiresAbsentClass").get();
        RequiresCondition annotationForAbsent = requiresAbsentClass.annotations().get(RequiresCondition.class).get();
        ConditionContext contextForAbsent = new ConditionContext(this.applicationContext, requiresAbsentClass, new AnnotationConditionDeclaration(annotationForAbsent));
        assertThat(condition.matches(contextForAbsent).matches()).isFalse();
    }

    @RequiresClass("java.lang.String")
    private void requiresClass() {}

    @RequiresClass("java.gnal.String")
    private void requiresAbsentClass() {}

    @Test
    void enclosedViewsIncludeParentCondition() {
        TypeView<ParentClass> type = this.applicationContext.environment().introspector().introspect(ParentClass.class);
        ConditionMatcher matcher = new ConditionMatcher(() -> this.applicationContext);
        assertThat(matcher.match(type)).isFalse();

        MethodView<ParentClass, ?> methodView = type.methods().named("requiresClass").get();
        matcher.includeEnclosingConditions(false);
        assertThat(matcher.match(methodView)).isTrue();

        matcher.includeEnclosingConditions(true);
        assertThat(matcher.match(methodView)).isFalse();
    }

    @RequiresClass("java.gnal.String")
    public static class ParentClass {
        @RequiresClass("java.lang.String")
        public void requiresClass() {}
    }
}
