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

package test.org.dockbox.hartshorn;

import org.dockbox.hartshorn.inject.Inject;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.Configuration;
import org.dockbox.hartshorn.inject.annotations.Prototype;
import org.dockbox.hartshorn.inject.annotations.Priority;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.testsuite.TestComponents;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import test.org.dockbox.hartshorn.components.SampleInterface;

@HartshornTest(includeBasePackages = false)
public class PriorityBindingTests {

    public static final String PRIORITY_DEFAULT = "Default";
    public static final String PRIORITY_ZERO = "Zero";
    public static final String PRIORITY_ONE = "One";

    @Inject
    private ApplicationContext applicationContext;

    @Test
    @TestComponents(components = { ZeroAndDefaultPriorityConfiguration.class, ImplicitPriorityConfiguration.class})
    void testProvisionWithImplicitPriority() {
        SampleInterface sampleInterface = this.applicationContext.get(SampleInterface.class);
        Assertions.assertEquals(PRIORITY_ZERO + PRIORITY_ONE, sampleInterface.name());
    }

    @Test
    @TestComponents(components = { ZeroAndDefaultPriorityConfiguration.class, ExplicitPriorityConfiguration.class})
    void testProvisionWithExplicitPriority() {
        SampleInterface sampleInterface = this.applicationContext.get(SampleInterface.class);
        Assertions.assertEquals(PRIORITY_DEFAULT + PRIORITY_ONE, sampleInterface.name());
    }

    @Configuration
    public static class ZeroAndDefaultPriorityConfiguration {

        @Prototype
        public SampleInterface sampleInterfaceDefaultPriority() {
            return () -> PRIORITY_DEFAULT;
        }

        @Prototype
        @Priority(0)
        public SampleInterface sampleInterfacePriorityZero() {
            return () -> PRIORITY_ZERO;
        }
    }

    @Configuration
    public static class ImplicitPriorityConfiguration {

        @Prototype
        @Priority(1)
        public SampleInterface sampleInterfacePriorityOne(SampleInterface lowerPriority) {
            return () -> lowerPriority.name() + PRIORITY_ONE;
        }
    }

    @Configuration
    public static class ExplicitPriorityConfiguration {

        @Prototype
        @Priority(1)
        public SampleInterface sampleInterfacePriorityOne(@Priority(Priority.DEFAULT_PRIORITY) SampleInterface lowerPriority) {
            return () -> lowerPriority.name() + PRIORITY_ONE;
        }
    }
}
