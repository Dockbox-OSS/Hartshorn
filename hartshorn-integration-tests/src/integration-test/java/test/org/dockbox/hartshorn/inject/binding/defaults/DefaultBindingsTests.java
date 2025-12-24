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

package test.org.dockbox.hartshorn.inject.binding.defaults;

import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.inject.component.ComponentContainer;
import org.dockbox.hartshorn.inject.component.ComponentRegistry;
import org.dockbox.hartshorn.test.annotations.TestProperties;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import static org.assertj.core.api.Assertions.assertThat;

@HartshornIntegrationTest(includeBasePackages = false)
class DefaultBindingsTests {

    @Inject
    private Logger loggerField;

    @Inject
    private ComponentRegistry componentRegistry;

    @Test
    @TestProperties("hartshorn.logging.naming.use-container-names=true")
    void loggerUsesContainerNameIfEnabled(@Inject Logger loggerParameter) {
        assertThat(loggerParameter).isNotNull();
        // Name should match the consuming class' name, and not the name of the configuration that uses it
        ComponentContainer<?> container =
            this.componentRegistry.container(this.getClass()).orElseGet(Assertions::fail);
        String expectedName = container.name();
        assertThat(loggerParameter.getName()).isEqualTo(expectedName);

        assertThat(this.loggerField).isNotNull();
        assertThat(this.loggerField.getName()).isEqualTo(expectedName);
    }

    @Test
    @TestProperties("hartshorn.logging.naming.use-container-names=false")
    void loggerUsesClassNameIfDisabled(@Inject Logger loggerParameter) {
        assertThat(loggerParameter).isNotNull();
        String expectedName = this.getClass().getName();
        assertThat(loggerParameter.getName()).isEqualTo(expectedName);

        assertThat(this.loggerField).isNotNull();
        assertThat(this.loggerField.getName()).isEqualTo(expectedName);
    }

    @Test
    void loggerUsesClassNameByDefault(@Inject Logger loggerParameter) {
        assertThat(loggerParameter).isNotNull();
        String expectedName = this.getClass().getName();
        assertThat(loggerParameter.getName()).isEqualTo(expectedName);

        assertThat(this.loggerField).isNotNull();
        assertThat(this.loggerField.getName()).isEqualTo(expectedName);
    }
}
