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

package test.org.dockbox.hartshorn.beans;

import org.dockbox.hartshorn.application.ApplicationBuilder;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.beans.BeanCollector;
import org.dockbox.hartshorn.beans.BeanContext;
import org.dockbox.hartshorn.beans.BeanProvider;
import org.dockbox.hartshorn.beans.BeanReference;
import org.dockbox.hartshorn.inject.Context;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.testsuite.InjectTest;
import org.dockbox.hartshorn.testsuite.ModifyApplication;
import org.dockbox.hartshorn.testsuite.TestComponents;
import org.dockbox.hartshorn.util.option.Option;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import jakarta.inject.Inject;
import test.org.dockbox.hartshorn.components.BeanAwareComponent;

@HartshornTest(includeBasePackages = false)
public class BeanTests {

    @ModifyApplication
    public static ApplicationBuilder<?, ?> builder(final ApplicationBuilder<?, ?> builder) {
        return builder.includeBasePackages(false).argument("--hartshorn:debug=true");
    }

    @Inject
    private ApplicationContext applicationContext;

    @Test
    void testBeanRegistrationCreatesValidReference() {
        final BeanCollector beanContext = this.applicationContext.first(BeanContext.CONTEXT_KEY).get();
        final BeanReference<String> reference = beanContext.register("John Doe", String.class, "names");

        Assertions.assertEquals("John Doe", reference.bean());
        Assertions.assertEquals("names", reference.id());
        Assertions.assertEquals(String.class, reference.type().type());
    }

    @Test
    void testBeanRegistrationCanBeProvided() {
        final BeanCollector beanContext = this.applicationContext.first(BeanContext.CONTEXT_KEY).get();
        beanContext.register(String.class, List.of("John", "Jane", "Joe"), "names");

        final BeanProvider beanProvider = this.applicationContext.get(BeanProvider.class);
        final List<String> names = beanProvider.all(String.class, "names");

        Assertions.assertEquals(3, names.size());
        Assertions.assertTrue(names.contains("John"));
        Assertions.assertTrue(names.contains("Jane"));
        Assertions.assertTrue(names.contains("Joe"));
    }

    @Test
    @TestComponents(BeanAwareComponent.class)
    void testComponentInjectionWithoutExplicitCollection() {
        final BeanCollector beanContext = this.applicationContext.first(BeanContext.CONTEXT_KEY).get();
        beanContext.register("Foo", String.class, "names");
        beanContext.register("Bar", String.class, "names");

        final BeanAwareComponent component = this.applicationContext.get(BeanAwareComponent.class);
        final List<String> names = component.names();

        Assertions.assertNotNull(names);
        Assertions.assertEquals(2, names.size());
        Assertions.assertTrue(names.contains("Foo"));
        Assertions.assertTrue(names.contains("Bar"));
    }

    @Test
    @TestComponents(BeanAwareComponent.class)
    void testComponentInjectionWithExplicitCollection() {
        final BeanContext beanContext = this.applicationContext.first(BeanContext.CONTEXT_KEY).get();
        beanContext.register(1, Integer.class, "ages");
        beanContext.register(2, Integer.class, "ages");

        final BeanAwareComponent component = this.applicationContext.get(BeanAwareComponent.class);
        final Set<Integer> ages = component.ages();

        Assertions.assertNotNull(ages);
        Assertions.assertTrue(ages instanceof TreeSet<Integer>);
        Assertions.assertEquals(2, ages.size());
        Assertions.assertTrue(ages.contains(1));
        Assertions.assertTrue(ages.contains(2));
    }

    @InjectTest
    @TestComponents(BeanService.class)
    void testApplicationHasBeanContext(final ApplicationContext applicationContext) {
        final Option<BeanContext> beanContext = applicationContext.first(BeanContext.CONTEXT_KEY);
        Assertions.assertTrue(beanContext.present());

        final BeanProvider provider = beanContext.get().provider();
        Assertions.assertNotNull(provider);
    }

    @InjectTest
    @TestComponents(BeanService.class)
    void testBeansAreCollected(@Context final BeanContext beanContext) {
        final BeanProvider beanProvider = beanContext.provider();
        final List<BeanObject> beanObjects = beanProvider.all(BeanObject.class);
        Assertions.assertEquals(3, beanObjects.size());

        final BeanObject user = beanProvider.first(BeanObject.class, "user");
        Assertions.assertNotNull(user);

        final BeanObject admin = beanProvider.first(BeanObject.class, "admin");
        Assertions.assertNotNull(admin);

        final BeanObject guest = beanProvider.first(BeanObject.class, "guest");
        Assertions.assertNotNull(guest);
    }

    @InjectTest
    @TestComponents({BeanService.class, TestBeanObserver.class })
    void testBeansAreObserved(final TestBeanObserver observer) {
        final List<BeanObject> beans = observer.beans();
        Assertions.assertEquals(3, beans.size());

        final BeanObject user = findBeanInList(beans, "user");
        Assertions.assertNotNull(user);

        final BeanObject admin = findBeanInList(beans, "admin");
        Assertions.assertNotNull(admin);

        final BeanObject guest = findBeanInList(beans, "guest");
        Assertions.assertNotNull(guest);
    }

    @Nullable
    private static BeanObject findBeanInList(final List<BeanObject> beans, final String user) {
        return beans.stream()
                .filter(bean -> user.equals(bean.name()))
                .findFirst()
                .orElse(null);
    }
}
