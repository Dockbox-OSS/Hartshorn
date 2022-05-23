package org.dockbox.hartshorn.data.service;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.processing.*;
import org.dockbox.hartshorn.data.annotations.ConfigurationObject;
import org.dockbox.hartshorn.data.annotations.UseConfigurations;
import org.dockbox.hartshorn.data.config.PropertyHolder;
import org.dockbox.hartshorn.inject.Key;
import org.dockbox.hartshorn.util.Exceptional;

@AutomaticActivation
public class ConfigurationObjectPostProcessor implements ComponentPostProcessor<UseConfigurations> {

    @Override
    public Class<UseConfigurations> activator() {
        return UseConfigurations.class;
    }

    @Override
    public <T> boolean modifies(ApplicationContext context, Key<T> key, @Nullable T instance, ComponentProcessingContext<T> processingContext) {
        return key.type().annotation(ConfigurationObject.class).present();
    }

    @Override
    public <T> T process(ApplicationContext context, Key<T> key, @Nullable T instance) {
        ConfigurationObject configurationObject = key.type().annotation(ConfigurationObject.class).get();
        PropertyHolder propertyHolder = context.get(PropertyHolder.class);

        Exceptional<T> configuration = propertyHolder.get(configurationObject.prefix(), key.type().type());
        return configuration.or(instance);
    }

    @Override
    public Integer order() {
        return ProcessingOrder.EARLY;
    }
}
