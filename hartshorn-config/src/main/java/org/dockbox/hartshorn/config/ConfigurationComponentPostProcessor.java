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

package org.dockbox.hartshorn.config;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.component.processing.ProcessingPriority;
import org.dockbox.hartshorn.config.annotations.Value;
import org.dockbox.hartshorn.config.properties.PropertyAwareComponentPostProcessor;
import org.dockbox.hartshorn.config.properties.PropertyHolder;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.NotPrimitiveException;
import org.dockbox.hartshorn.util.introspect.view.FieldView;
import org.dockbox.hartshorn.util.option.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Looks up and populates fields annotated with {@link Value}.
 */
public class ConfigurationComponentPostProcessor extends PropertyAwareComponentPostProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationComponentPostProcessor.class);

    @Override
    public <T> void postConfigureComponent(ApplicationContext context, @Nullable T instance, ComponentProcessingContext<T> processingContext)
            throws ApplicationException {
        if (processingContext.type().fields().annotatedWith(Value.class).isEmpty()) {
            return;
        }

        PropertyHolder propertyHolder = context.get(PropertyHolder.class);
        this.verifyPropertiesAvailable(context, propertyHolder);

        for (FieldView<T, ?> field : processingContext.type().fields().annotatedWith(Value.class)) {
            try {
                Value annotation = field.annotations().get(Value.class).get();

                String valueKey = annotation.value();
                Option<?> property = propertyHolder.get(valueKey, field.genericType().type());

                if (property.absent()) {
                    LOG.debug("Property {} for field {} is empty, but field has a default value, using default value (note this may be null)", valueKey, field.name());
                    continue;
                }

                LOG.debug("Populating value for configuration field '{}' in {} (key: {}), value is not logged.", field.name(), valueKey, field.type().name());
                field.set(instance, property.get());
            }
            catch (NotPrimitiveException e) {
                LOG.warn("Could not prepare value field {} in {}", field.name(), processingContext.type().name());
                context.handle(e);
            }
            catch(Throwable e) {
                throw new ApplicationException(e);
            }
        }
    }

    @Override
    public int priority() {
        return ProcessingPriority.NORMAL_PRECEDENCE;
    }
}
