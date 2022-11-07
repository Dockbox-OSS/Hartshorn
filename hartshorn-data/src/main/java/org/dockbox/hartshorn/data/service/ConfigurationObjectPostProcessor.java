/*
 * Copyright 2019-2022 the original author or authors.
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

package org.dockbox.hartshorn.data.service;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.component.processing.ProcessingOrder;
import org.dockbox.hartshorn.data.annotations.ConfigurationObject;
import org.dockbox.hartshorn.data.config.PropertyHolder;
import org.dockbox.hartshorn.inject.Key;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.option.Option;

public class ConfigurationObjectPostProcessor extends PropertyAwareComponentPostProcessor {

    @Override
    public <T> T process(final ApplicationContext context, @Nullable final T instance, final ComponentProcessingContext<T> processingContext) {
        if (processingContext.type().annotations().has(ConfigurationObject.class)) {
            final ConfigurationObject configurationObject = processingContext.type().annotations().get(ConfigurationObject.class).get();

            final PropertyHolder propertyHolder = context.get(PropertyHolder.class);
            this.verifyPropertiesAvailable(context, propertyHolder);

            return this.createOrUpdate(processingContext.key(), instance, configurationObject, propertyHolder, context);
        }
        return instance;
    }

    private <T> T createOrUpdate(final Key<T> key, final T instance, final ConfigurationObject configurationObject, final PropertyHolder propertyHolder, final ApplicationContext applicationContext) {
        final Option<T> configuration;
        final Class<T> type = instance == null
                ? key.type()
                : TypeUtils.adjustWildcards(instance.getClass(), Class.class);

        if (instance == null) {
            configuration = propertyHolder.get(configurationObject.prefix(), type);
        }
        else {
            configuration = propertyHolder.update(instance, configurationObject.prefix(), type);
        }
        return configuration.orElse(instance);
    }

    @Override
    public Integer order() {
        return ProcessingOrder.FIRST;
    }
}
