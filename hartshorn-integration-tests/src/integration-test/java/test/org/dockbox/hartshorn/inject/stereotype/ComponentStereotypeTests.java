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

package test.org.dockbox.hartshorn.inject.stereotype;

import org.dockbox.hartshorn.inject.ComponentResolutionException;
import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.test.annotations.TestComponents;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

@HartshornIntegrationTest(includeBasePackages = false)
class ComponentStereotypeTests {

    @Inject
    private ApplicationContext applicationContext;

    @Test
    @TestComponents(EmptyComponent.class)
    void servicesAreSingletonsByDefault() {
        EmptyComponent emptyComponent = this.applicationContext.get(EmptyComponent.class);
        EmptyComponent emptyComponent2 = this.applicationContext.get(EmptyComponent.class);
        assertThat(emptyComponent2).isSameAs(emptyComponent);
    }

    @Test
    void nonComponentsAreNotProxied() {
        assertThatExceptionOfType(ComponentResolutionException.class).isThrownBy(() -> this.applicationContext.get(NonComponentType.class));
    }

    @Test
    @TestComponents(ComponentType.class)
    void permittedComponentsAreProxiedWhenRegularProvisionFails() {
        ComponentType instance = this.applicationContext.get(ComponentType.class);
        assertThat(instance).isNotNull();
        assertThat(this.applicationContext.environment()
            .proxyOrchestrator()
            .isProxy(instance)).isTrue();
    }

    @Test
    @TestComponents(NonProxyComponentType.class)
    void nonPermittedComponentsAreNotProxied() {
        assertThatExceptionOfType(ComponentResolutionException.class).isThrownBy(() -> this.applicationContext.get(NonProxyComponentType.class));
    }
}
