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

package test.org.dockbox.hartshorn.inject.strategies;

import org.dockbox.hartshorn.inject.ComponentResolutionException;
import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.test.annotations.TestComponents;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.junit.jupiter.api.Test;

import test.org.dockbox.hartshorn.inject.stereotype.ComponentType;
import test.org.dockbox.hartshorn.inject.context.SampleContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

@HartshornIntegrationTest(includeBasePackages = false)
class SetterInjectionTests {

    @Inject
    private ApplicationContext applicationContext;

    @Test
    @TestComponents({ SetterInjectedComponent.class, ComponentType.class})
    void setterInjectionWithRegularComponent() {
        SetterInjectedComponent component = this.applicationContext.get(SetterInjectedComponent.class);
        assertThat(component).isNotNull();
        assertThat(component.component()).isNotNull();
    }

    @Test
    @TestComponents(SetterInjectedComponentWithAbsentBinding.class)
    void setterInjectionWithAbsentRequiredComponent() {
        assertThatExceptionOfType(ComponentResolutionException.class).isThrownBy(() -> this.applicationContext.get(SetterInjectedComponentWithAbsentBinding.class));
    }

    @Test
    @TestComponents(SetterInjectedComponentWithNonRequiredAbsentBinding.class)
    void setterInjectionWithAbsentComponent() {
        assertThatCode(() -> {
            var component = this.applicationContext.get(SetterInjectedComponentWithNonRequiredAbsentBinding.class);
            assertThat(component).isNotNull();
            assertThat(component.object()).isNull();
        }).doesNotThrowAnyException();
    }

    @Test
    @TestComponents({SetterInjectedComponent.class, ComponentType.class})
    void setterInjectionWithContext() {
        SampleContext sampleContext = new SampleContext("setter");
        this.applicationContext.addContext("setter", sampleContext);
        SetterInjectedComponent component = this.applicationContext.get(SetterInjectedComponent.class);
        assertThat(component).isNotNull();
        assertThat(component.context()).isNotNull();
        assertThat(component.context()).isSameAs(sampleContext);
    }
}
