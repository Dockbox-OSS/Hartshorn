package org.dockbox.hartshorn.core.types;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.core.Key;
import org.dockbox.hartshorn.core.annotations.activate.AutomaticActivation;
import org.dockbox.hartshorn.core.annotations.stereotype.Service;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.services.ComponentPostProcessor;

@AutomaticActivation
public class NonProcessableTypeProcessor implements ComponentPostProcessor<Service> {
    @Override
    public Class<Service> activator() {
        return Service.class;
    }

    @Override
    public <T> T process(final ApplicationContext context, final Key<T> key, @Nullable final T instance) {
        final TypeContext<T> type = key.type();
        type.field("nonNullIfProcessed").get().set(instance, "processed");
        return instance;
    }

    @Override
    public <T> boolean modifies(final ApplicationContext context, final Key<T> key, @Nullable final T instance) {
        return instance instanceof NonProcessableType;
    }
}
