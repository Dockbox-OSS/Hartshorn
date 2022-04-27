package org.dockbox.hartshorn.application.context;

import org.dockbox.hartshorn.inject.binding.BindingFunction;
import org.dockbox.hartshorn.util.reflect.TypeContext;

import java.util.function.Supplier;

public class DelegatingApplicationBindingFunction<T> implements BindingFunction<T> {

    private final ApplicationContext applicationContext;
    private final BindingFunction<T> delegate;

    public DelegatingApplicationBindingFunction(final ApplicationContext applicationContext, final BindingFunction<T> delegate) {
        this.applicationContext = applicationContext;
        this.delegate = delegate;
    }

    @Override
    public ApplicationContext to(final Class<? extends T> type) {
        this.delegate.to(type);
        return this.applicationContext;
    }

    @Override
    public ApplicationContext to(final TypeContext<? extends T> type) {
        this.delegate.to(type);
        return this.applicationContext;
    }

    @Override
    public ApplicationContext to(final Supplier<T> supplier) {
        this.delegate.to(supplier);
        return this.applicationContext;
    }

    @Override
    public ApplicationContext singleton(final T t) {
        this.delegate.singleton(t);
        return this.applicationContext;
    }

    @Override
    public ApplicationContext lazySingleton(final Class<T> type) {
        this.delegate.lazySingleton(type);
        return this.applicationContext;
    }

    @Override
    public ApplicationContext lazySingleton(final TypeContext<T> type) {
        this.delegate.lazySingleton(type);
        return this.applicationContext;
    }

    @Override
    public ApplicationContext lazySingleton(final Supplier<T> supplier) {
        this.delegate.lazySingleton(supplier);
        return this.applicationContext;
    }
}
