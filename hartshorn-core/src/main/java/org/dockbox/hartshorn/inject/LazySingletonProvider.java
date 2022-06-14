package org.dockbox.hartshorn.inject;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.util.Result;

import java.util.function.Supplier;

public class LazySingletonProvider<T> implements Provider<T> {

    private final Supplier<ObjectContainer<T>> supplier;
    private ObjectContainer<T> container;

    public LazySingletonProvider(final Supplier<ObjectContainer<T>> supplier) {
        this.supplier = supplier;
    }

    @Override
    public Result<ObjectContainer<T>> provide(final ApplicationContext context) {
        if (this.container == null) {
            this.container = this.supplier.get();
        }
        return Result.of(this.container);
    }
}
