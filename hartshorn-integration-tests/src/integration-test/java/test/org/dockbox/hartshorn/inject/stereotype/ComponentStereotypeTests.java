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

package test.org.dockbox.hartshorn.inject.stereotype;

import org.dockbox.hartshorn.inject.ComponentResolutionException;
import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.test.annotations.TestComponents;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@HartshornIntegrationTest(includeBasePackages = false)
public class ComponentStereotypeTests {

    @Inject
    private ApplicationContext applicationContext;

    @Test
    @TestComponents(components = EmptyService.class)
    void servicesAreSingletonsByDefault() {
        EmptyService emptyService = this.applicationContext.get(EmptyService.class);
        EmptyService emptyService2 = this.applicationContext.get(EmptyService.class);
        Assertions.assertSame(emptyService, emptyService2);
    }

    @Test
    void testNonComponentsAreNotProxied() {
        Assertions.assertThrows(ComponentResolutionException.class, () -> this.applicationContext.get(NonComponentType.class));
    }

    @Test
    @TestComponents(components = ComponentType.class)
    void testPermittedComponentsAreProxiedWhenRegularProvisionFails() {
        ComponentType instance = this.applicationContext.get(ComponentType.class);
        Assertions.assertNotNull(instance);
        Assertions.assertTrue(this.applicationContext.environment().proxyOrchestrator().isProxy(instance));
    }

    @Test
    @TestComponents(components = NonProxyComponentType.class)
    void testNonPermittedComponentsAreNotProxied() {
        Assertions.assertThrows(ComponentResolutionException.class, () -> this.applicationContext.get(NonProxyComponentType.class));
    }
}
