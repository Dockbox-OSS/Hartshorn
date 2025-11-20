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

package org.dockbox.hartshorn.test;

import org.dockbox.hartshorn.launchpad.SimpleApplicationContext;
import org.dockbox.hartshorn.launchpad.environment.ConfigurableApplicationEnvironment;
import org.dockbox.hartshorn.launchpad.launch.StandardApplicationBuilder;
import org.dockbox.hartshorn.launchpad.launch.StandardApplicationContextFactory;

import java.util.List;

/**
 * Composite implementation of {@link TestApplicationCustomizer}, executing the given customizers in
 * the order they were provided.
 *
 * @author Guus Lieben
 * @since 0.7.0
 */
public class CompositeTestApplicationCustomizer implements TestApplicationCustomizer {

    private final List<TestApplicationCustomizer> customizers;

    public CompositeTestApplicationCustomizer(List<TestApplicationCustomizer> customizers) {
        this.customizers = customizers;
    }

    @Override
    public void customizeBuilder(StandardApplicationBuilder.Configurer configurer) {
        this.customizers.forEach(customizer -> customizer.customizeBuilder(configurer));
    }

    @Override
    public void customizeEnvironment(ConfigurableApplicationEnvironment.Configurer configurer) {
        this.customizers.forEach((customizer) -> customizer.customizeEnvironment(configurer));
    }

    @Override
    public void customizeFactory(StandardApplicationContextFactory.Configurer configurer) {
        this.customizers.forEach((customizer) -> customizer.customizeFactory(configurer));
    }

    @Override
    public void customizeApplication(SimpleApplicationContext.Configurer configurer) {
        this.customizers.forEach((customizer) -> customizer.customizeApplication(configurer));
    }
}
