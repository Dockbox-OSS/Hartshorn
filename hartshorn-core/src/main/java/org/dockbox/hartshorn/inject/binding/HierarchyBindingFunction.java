package org.dockbox.hartshorn.inject.binding;

import org.dockbox.hartshorn.inject.ContextDrivenProvider;
import org.dockbox.hartshorn.inject.Key;
import org.dockbox.hartshorn.inject.SupplierProvider;
import org.dockbox.hartshorn.util.Exceptional;
import org.dockbox.hartshorn.util.reflect.TypeContext;

import java.util.function.Supplier;

public class HierarchyBindingFunction<T> implements BindingFunction<T> {

    private final BindingHierarchy<T> hierarchy;
    private final Binder binder;
    private final SingletonCache singletonCache;
    private final ComponentInstanceFactory instanceFactory;

    public HierarchyBindingFunction(
            final BindingHierarchy<T> hierarchy,
            final Binder binder,
            final SingletonCache singletonCache,
            final ComponentInstanceFactory instanceFactory
    ) {
        this.hierarchy = hierarchy;
        this.binder = binder;
        this.singletonCache = singletonCache;
        this.instanceFactory = instanceFactory;
    }

    protected BindingHierarchy<T> hierarchy() {
        return this.hierarchy;
    }

    protected Binder binder() {
        return this.binder;
    }

    protected SingletonCache singletonCache() {
        return this.singletonCache;
    }

    protected ComponentInstanceFactory instanceFactory() {
        return this.instanceFactory;
    }

    @Override
    public Binder to(final Class<? extends T> type) {
        return this.to(TypeContext.of(type));
    }

    @Override
    public Binder to(final TypeContext<? extends T> type) {
        this.hierarchy().add(new ContextDrivenProvider<>(type));
        return this.binder();
    }

    @Override
    public Binder to(final Supplier<T> supplier) {
        this.hierarchy().add(new SupplierProvider<>(supplier));
        return this.binder();
    }

    @Override
    public Binder singleton(final T t) {
        this.singletonCache().put(this.hierarchy().key(), t);
        return this.binder();
    }

    @Override
    public Binder lazySingleton(final Class<T> type) {
        return this.lazySingleton(TypeContext.of(type));
    }

    @Override
    public Binder lazySingleton(final TypeContext<T> type) {
        this.lazySingleton(() -> {
            final Key<T> key = Key.of(type);
            final Exceptional<T> object = this.instanceFactory().instantiate(key);
            return object.rethrowUnchecked().orNull();
        });
        return this.binder();
    }

    @Override
    public Binder lazySingleton(final Supplier<T> supplier) {
        final Key<T> key = this.hierarchy().key();
        this.hierarchy().add(new SupplierProvider<>(() -> {
            if (this.singletonCache.contains(key)) {
                return this.singletonCache.get(key);
            }
            final T t = supplier.get();
            this.singletonCache.put(key, t);
            return t;
        }));
        return this.binder();
    }
}
