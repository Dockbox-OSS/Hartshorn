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

package test.org.dockbox.hartshorn.components.contextual;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.contextual.StaticComponentCollector;
import org.dockbox.hartshorn.component.contextual.StaticComponentContainer;
import org.dockbox.hartshorn.component.contextual.StaticComponentContext;
import org.dockbox.hartshorn.component.contextual.StaticComponentProvider;
import org.dockbox.hartshorn.inject.Context;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.testsuite.InjectTest;
import org.dockbox.hartshorn.testsuite.ModifyApplication;
import org.dockbox.hartshorn.testsuite.TestComponents;
import org.dockbox.hartshorn.testsuite.TestCustomizer;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;
import test.org.dockbox.hartshorn.components.StaticAwareComponent;

@HartshornTest(includeBasePackages = false)
public class StaticBindsTests {

    @ModifyApplication
    public static void customize() {
        TestCustomizer.BUILDER.compose(builder -> builder.arguments("--hartshorn:debug=true"));
        TestCustomizer.CONSTRUCTOR.compose(constructor -> constructor.includeBasePackages(false));
    }

    @Inject
    private ApplicationContext applicationContext;

    @Test
    void testBeanRegistrationCreatesValidReference() {
        final StaticComponentCollector beanContext = this.applicationContext.first(StaticComponentContext.CONTEXT_KEY).get();
        final StaticComponentContainer<String> reference = beanContext.register("John Doe", String.class, "names");

        Assertions.assertEquals("John Doe", reference.instance());
        Assertions.assertEquals("names", reference.id());
        Assertions.assertSame(String.class, reference.type().type());
    }

    @Test
    void testBeanRegistrationCanBeProvided() {
        final StaticComponentCollector beanContext = this.applicationContext.first(StaticComponentContext.CONTEXT_KEY).get();
        beanContext.register(String.class, List.of("John", "Jane", "Joe"), "names");

        final StaticComponentProvider staticComponentProvider = this.applicationContext.get(StaticComponentProvider.class);
        final List<String> names = staticComponentProvider.all(String.class, "names");

        Assertions.assertEquals(3, names.size());
        Assertions.assertTrue(names.contains("John"));
        Assertions.assertTrue(names.contains("Jane"));
        Assertions.assertTrue(names.contains("Joe"));
    }

    @Test
    @TestComponents(components = StaticAwareComponent.class)
    void testComponentInjectionWithoutExplicitCollection() {
        final StaticComponentCollector beanContext = this.applicationContext.first(StaticComponentContext.CONTEXT_KEY).get();
        beanContext.register("Foo", String.class, "names");
        beanContext.register("Bar", String.class, "names");

        final StaticAwareComponent component = this.applicationContext.get(StaticAwareComponent.class);
        final List<String> names = component.names();

        Assertions.assertNotNull(names);
        Assertions.assertEquals(2, names.size());
        Assertions.assertTrue(names.contains("Foo"));
        Assertions.assertTrue(names.contains("Bar"));
    }

    @Test
    @TestComponents(components = StaticAwareComponent.class)
    void testComponentInjectionWithExplicitCollection() {
        final StaticComponentContext staticComponentContext = this.applicationContext.first(StaticComponentContext.CONTEXT_KEY).get();
        staticComponentContext.register(1, Integer.class, "ages");
        staticComponentContext.register(2, Integer.class, "ages");

        final StaticAwareComponent component = this.applicationContext.get(StaticAwareComponent.class);
        final Set<Integer> ages = component.ages();

        Assertions.assertNotNull(ages);
        Assertions.assertTrue(ages instanceof TreeSet<Integer>);
        Assertions.assertEquals(2, ages.size());
        Assertions.assertTrue(ages.contains(1));
        Assertions.assertTrue(ages.contains(2));
    }

    @InjectTest
    @TestComponents(components = StaticComponentService.class)
    void testApplicationHasBeanContext(final ApplicationContext applicationContext) {
        final Option<StaticComponentContext> beanContext = applicationContext.first(StaticComponentContext.CONTEXT_KEY);
        Assertions.assertTrue(beanContext.present());

        final StaticComponentProvider provider = beanContext.get().provider();
        Assertions.assertNotNull(provider);
    }

    @InjectTest
    @TestComponents(components = StaticComponentService.class)
    void testBeansAreCollected(@Context final StaticComponentContext staticComponentContext) {
        final StaticComponentProvider staticComponentProvider = staticComponentContext.provider();
        final List<StaticComponent> staticComponents = staticComponentProvider.all(StaticComponent.class);
        Assertions.assertEquals(3, staticComponents.size());

        final StaticComponent user = staticComponentProvider.first(StaticComponent.class, "user");
        Assertions.assertNotNull(user);

        final StaticComponent admin = staticComponentProvider.first(StaticComponent.class, "admin");
        Assertions.assertNotNull(admin);

        final StaticComponent guest = staticComponentProvider.first(StaticComponent.class, "guest");
        Assertions.assertNotNull(guest);
    }

    @InjectTest
    @TestComponents(components = { StaticComponentService.class, TestStaticComponentObserver.class })
    void testBeansAreObserved(final TestStaticComponentObserver observer) {
        final List<StaticComponent> beans = observer.components();
        Assertions.assertEquals(3, beans.size());

        final StaticComponent user = findBeanInList(beans, "user");
        Assertions.assertNotNull(user);

        final StaticComponent admin = findBeanInList(beans, "admin");
        Assertions.assertNotNull(admin);

        final StaticComponent guest = findBeanInList(beans, "guest");
        Assertions.assertNotNull(guest);
    }

    @Nullable
    private static StaticComponent findBeanInList(final List<StaticComponent> beans, final String user) {
        return beans.stream()
                .filter(bean -> user.equals(bean.name()))
                .findFirst()
                .orElse(null);
    }
}
