package org.dockbox.hartshorn.inject;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.util.Result;

public class SingletonProvider<T> implements Provider<T> {

    private final ObjectContainer<T> container;

    public SingletonProvider(final T instance) {
        this(instance, false);
    }

    public SingletonProvider(final T instance, final boolean processed) {
        this.container = new ObjectContainer<>(instance, processed);
    }

    @Override
    public Result<ObjectContainer<T>> provide(final ApplicationContext context) {
        return Result.of(this.container);
    }
}
