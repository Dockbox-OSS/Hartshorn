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

package test.org.dockbox.hartshorn.inject.binding.priority;

import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.inject.annotations.configuration.Configuration;
import org.dockbox.hartshorn.inject.annotations.configuration.Prototype;
import org.dockbox.hartshorn.inject.annotations.Priority;
import org.dockbox.hartshorn.test.annotations.TestComponents;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@HartshornIntegrationTest(includeBasePackages = false)
public class PriorityBindingTests {

    public static final String PRIORITY_DEFAULT = "Default";
    public static final String PRIORITY_ZERO = "Zero";
    public static final String PRIORITY_ONE = "One";

    @Inject
    private ApplicationContext applicationContext;

    @Test
    @TestComponents(components = { ZeroAndDefaultPriorityConfiguration.class, ImplicitPriorityConfiguration.class})
    void testProvisionWithImplicitPriority() {
        TestPriorityComponent component = this.applicationContext.get(TestPriorityComponent.class);
        Assertions.assertEquals(PRIORITY_ZERO + PRIORITY_ONE, component.name());
    }

    @Test
    @TestComponents(components = { ZeroAndDefaultPriorityConfiguration.class, ExplicitPriorityConfiguration.class})
    void testProvisionWithExplicitPriority() {
        TestPriorityComponent component = this.applicationContext.get(TestPriorityComponent.class);
        Assertions.assertEquals(PRIORITY_DEFAULT + PRIORITY_ONE, component.name());
    }

    @Test
    void testPrioritySingletonBinding() {
        this.applicationContext.bind(String.class).singleton("Hello world!");
        this.applicationContext.bind(String.class).priority(0).singleton("Hello modified world!");

        String binding = this.applicationContext.get(String.class);
        Assertions.assertEquals("Hello modified world!", binding);

        this.applicationContext.bind(String.class).priority(-2).singleton("Hello low priority world!");
        String binding2 = this.applicationContext.get(String.class);
        Assertions.assertEquals("Hello modified world!", binding2);
    }

    @Test
    void testPrioritySupplierBinding() {
        this.applicationContext.bind(String.class).to(() -> "Hello world!");
        this.applicationContext.bind(String.class).priority(0).to(() -> "Hello modified world!");

        String binding = this.applicationContext.get(String.class);
        Assertions.assertEquals("Hello modified world!", binding);

        this.applicationContext.bind(String.class).priority(-2).to(() -> "Hello low priority world!");
        String binding2 = this.applicationContext.get(String.class);
        Assertions.assertEquals("Hello modified world!", binding2);
    }

    @Configuration
    public static class ZeroAndDefaultPriorityConfiguration {

        @Prototype
        public TestPriorityComponent sampleInterfaceDefaultPriority() {
            return () -> PRIORITY_DEFAULT;
        }

        @Prototype
        @Priority(0)
        public TestPriorityComponent sampleInterfacePriorityZero() {
            return () -> PRIORITY_ZERO;
        }
    }

    @Configuration
    public static class ImplicitPriorityConfiguration {

        @Prototype
        @Priority(1)
        public TestPriorityComponent sampleInterfacePriorityOne(TestPriorityComponent lowerPriority) {
            return () -> lowerPriority.name() + PRIORITY_ONE;
        }
    }

    @Configuration
    public static class ExplicitPriorityConfiguration {

        @Prototype
        @Priority(1)
        public TestPriorityComponent sampleInterfacePriorityOne(@Priority(Priority.DEFAULT_PRIORITY) TestPriorityComponent lowerPriority) {
            return () -> lowerPriority.name() + PRIORITY_ONE;
        }
    }
}
