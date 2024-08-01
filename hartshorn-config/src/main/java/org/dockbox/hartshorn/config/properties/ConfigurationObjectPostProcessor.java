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

package org.dockbox.hartshorn.config.properties;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.component.processing.ProcessingPriority;
import org.dockbox.hartshorn.config.ObjectMappingException;
import org.dockbox.hartshorn.config.annotations.ConfigurationObject;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.option.Option;

/**
 * TODO: #1062 Add documentation
 *
 * @since 0.4.11
 *
 * @author Guus Lieben
 */
public class ConfigurationObjectPostProcessor extends PropertyAwareComponentPostProcessor {

    private final PropertyHolder propertyHolder;

    public ConfigurationObjectPostProcessor(PropertyHolder propertyHolder) {
        this.propertyHolder = propertyHolder;
    }

    @Override
    public <T> T initializeComponent(ApplicationContext context, @Nullable T instance, ComponentProcessingContext<T> processingContext) throws ObjectMappingException {
        if (processingContext.type().annotations().has(ConfigurationObject.class)) {
            ConfigurationObject configurationObject = processingContext.type().annotations().get(ConfigurationObject.class).get();

            this.verifyPropertiesAvailable(context, this.propertyHolder);

            return this.createOrUpdate(processingContext.key(), instance, configurationObject);
        }
        return instance;
    }

    private <T> T createOrUpdate(ComponentKey<T> key, T instance, ConfigurationObject configurationObject) throws ObjectMappingException {
        Option<T> configuration;
        Class<T> type = instance == null
                ? key.type()
                : TypeUtils.getClass(instance);

        if (instance == null) {
            configuration = this.propertyHolder.get(configurationObject.prefix(), type);
        }
        else {
            configuration = this.propertyHolder.update(instance, configurationObject.prefix(), type);
        }
        return configuration.orElse(instance);
    }

    @Override
    public int priority() {
        return ProcessingPriority.HIGHEST_PRECEDENCE;
    }
}
