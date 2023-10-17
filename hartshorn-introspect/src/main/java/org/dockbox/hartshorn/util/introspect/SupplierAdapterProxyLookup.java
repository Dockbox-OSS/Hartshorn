package org.dockbox.hartshorn.util.introspect;

import java.util.function.Supplier;

import org.dockbox.hartshorn.util.option.Option;

public class SupplierAdapterProxyLookup implements ProxyLookup {

    private final Supplier<ProxyLookup> delegate;

    public SupplierAdapterProxyLookup(Supplier<ProxyLookup> delegate) {
        this.delegate = delegate;
    }

    @Override
    public <T> Option<Class<T>> unproxy(T instance) {
        return this.delegate.get().unproxy(instance);
    }

    @Override
    public boolean isProxy(Object instance) {
        return this.delegate.get().isProxy(instance);
    }

    @Override
    public boolean isProxy(Class<?> candidate) {
        return this.delegate.get().isProxy(candidate);
    }

    @Override
    public <T> Option<ProxyIntrospector<T>> introspector(T instance) {
        return this.delegate.get().introspector(instance);
    }
}
