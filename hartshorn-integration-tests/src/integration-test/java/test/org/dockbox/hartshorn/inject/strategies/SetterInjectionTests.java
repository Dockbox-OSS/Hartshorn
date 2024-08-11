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

package test.org.dockbox.hartshorn.inject.strategies;

import org.dockbox.hartshorn.inject.ComponentResolutionException;
import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.test.annotations.TestComponents;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import test.org.dockbox.hartshorn.inject.stereotype.ComponentType;
import test.org.dockbox.hartshorn.inject.context.SampleContext;

@HartshornIntegrationTest(includeBasePackages = false)
public class SetterInjectionTests {

    @Inject
    private ApplicationContext applicationContext;

    @Test
    @TestComponents(components = { SetterInjectedComponent.class, ComponentType.class})
    void testSetterInjectionWithRegularComponent() {
        SetterInjectedComponent component = this.applicationContext.get(SetterInjectedComponent.class);
        Assertions.assertNotNull(component);
        Assertions.assertNotNull(component.component());
    }

    @Test
    @TestComponents(components = SetterInjectedComponentWithAbsentBinding.class)
    void testSetterInjectionWithAbsentRequiredComponent() {
        Assertions.assertThrows(ComponentResolutionException.class, () -> this.applicationContext.get(SetterInjectedComponentWithAbsentBinding.class));
    }

    @Test
    @TestComponents(components = SetterInjectedComponentWithNonRequiredAbsentBinding.class)
    void testSetterInjectionWithAbsentComponent() {
        var component = Assertions.assertDoesNotThrow(() -> this.applicationContext.get(SetterInjectedComponentWithNonRequiredAbsentBinding.class));
        Assertions.assertNotNull(component);
        Assertions.assertNull(component.object());
    }

    @Test
    @TestComponents(components = {SetterInjectedComponent.class, ComponentType.class})
    void testSetterInjectionWithContext() {
        SampleContext sampleContext = new SampleContext("setter");
        this.applicationContext.addContext("setter", sampleContext);
        SetterInjectedComponent component = this.applicationContext.get(SetterInjectedComponent.class);
        Assertions.assertNotNull(component);
        Assertions.assertNotNull(component.context());
        Assertions.assertSame(sampleContext, component.context());
    }
}
