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
import org.dockbox.hartshorn.component.processing.ComponentPostProcessor;
import org.dockbox.hartshorn.component.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.component.processing.ProcessingOrder;
import org.dockbox.hartshorn.data.annotations.ConfigurationObject;
import org.dockbox.hartshorn.data.config.PropertyHolder;
import org.dockbox.hartshorn.data.config.URIConfigProcessor;
import org.dockbox.hartshorn.data.context.ConfigurationURIContextList;
import org.dockbox.hartshorn.inject.Key;
import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.util.reflect.TypeContext;

public class ConfigurationObjectPostProcessor implements ComponentPostProcessor {

    @Override
    public <T> boolean modifies(final ApplicationContext context, final Key<T> key, @Nullable final T instance, final ComponentProcessingContext processingContext) {
        if (instance != null && TypeContext.of(instance).annotation(ConfigurationObject.class).present()) {
            return true;
        }
        return key.type().annotation(ConfigurationObject.class).present();
    }

    @Override
    public <T> T process(final ApplicationContext context, final Key<T> key, @Nullable final T instance) {
        final ConfigurationObject configurationObject = key.type().annotation(ConfigurationObject.class)
                .orElse(() -> TypeContext.of(instance).annotation(ConfigurationObject.class).orNull())
                .get();

        final PropertyHolder propertyHolder = context.get(PropertyHolder.class);
        this.verifyPropertiesAvailable(context, propertyHolder);

        return this.createOrUpdate(key, instance, configurationObject, propertyHolder);
    }

    private <T> T createOrUpdate(final Key<T> key, final T instance, final ConfigurationObject configurationObject, final PropertyHolder propertyHolder) {
        final Result<T> configuration;
        if (instance == null) {
            configuration = propertyHolder.get(configurationObject.prefix(), key.type().type());
        }
        else {
            configuration = propertyHolder.update(instance, configurationObject.prefix(), key.type().type());
        }
        return configuration.rethrowUnchecked().or(instance);
    }

    private void verifyPropertiesAvailable(final ApplicationContext context, final PropertyHolder propertyHolder) {
        if (propertyHolder.properties().isEmpty()) {
            final ConfigurationURIContextList uriContextList = context.first(ConfigurationURIContextList.class).get();
            final URIConfigProcessor configProcessor = context.get(URIConfigProcessor.class);
            configProcessor.process(context, uriContextList.uris());
        }
    }

    @Override
    public Integer order() {
        return ProcessingOrder.FIRST;
    }
}
