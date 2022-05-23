package org.dockbox.hartshorn.data.service;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.processing.ComponentPostProcessor;
import org.dockbox.hartshorn.component.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.component.processing.ProcessingOrder;
import org.dockbox.hartshorn.data.annotations.ConfigurationObject;
import org.dockbox.hartshorn.data.annotations.UseConfigurations;
import org.dockbox.hartshorn.data.config.PropertyHolder;
import org.dockbox.hartshorn.inject.Key;
import org.dockbox.hartshorn.util.Result;

public class ConfigurationObjectPostProcessor implements ComponentPostProcessor<UseConfigurations> {

    @Override
    public <T> boolean modifies(final ApplicationContext context, final Key<T> key, @Nullable final T instance, final ComponentProcessingContext<T> processingContext) {
        return key.type().annotation(ConfigurationObject.class).present();
    }

    @Override
    public <T> T process(final ApplicationContext context, final Key<T> key, @Nullable final T instance) {
        final ConfigurationObject configurationObject = key.type().annotation(ConfigurationObject.class).get();
        final PropertyHolder propertyHolder = context.get(PropertyHolder.class);

        final Result<T> configuration = propertyHolder.get(configurationObject.prefix(), key.type().type());
        return configuration.or(instance);
    }

    @Override
    public Integer order() {
        return ProcessingOrder.EARLY;
    }
}
