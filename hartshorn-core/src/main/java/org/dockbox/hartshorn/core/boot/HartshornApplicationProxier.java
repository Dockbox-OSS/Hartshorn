package org.dockbox.hartshorn.core.boot;

import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.proxy.NativeProxyLookup;
import org.dockbox.hartshorn.core.proxy.Proxy;
import org.dockbox.hartshorn.core.proxy.ProxyLookup;
import org.dockbox.hartshorn.core.proxy.ProxyManager;
import org.dockbox.hartshorn.core.proxy.StateAwareProxyFactory;
import org.dockbox.hartshorn.core.proxy.javassist.JavassistProxyFactory;
import org.dockbox.hartshorn.core.proxy.javassist.JavassistProxyLookup;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Getter;

public class HartshornApplicationProxier implements ApplicationProxier, ApplicationManaged {

    @Getter
    private ApplicationManager applicationManager;
    private final Set<ProxyLookup> proxyLookups = ConcurrentHashMap.newKeySet();

    public HartshornApplicationProxier() {
        this.proxyLookups.add(new NativeProxyLookup());
        this.proxyLookups.add(new JavassistProxyLookup());
    }

    @Override
    public void applicationManager(final ApplicationManager applicationManager) {
        if (this.applicationManager == null) this.applicationManager = applicationManager;
        else throw new IllegalArgumentException("Application manager has already been configured");
    }

    @Override
    public <T> Exceptional<TypeContext<T>> real(final T instance) {
        if (instance instanceof Proxy proxy) {
            return Exceptional.of(TypeContext.of(proxy.manager().targetClass()));
        }
        return Exceptional.empty();
    }

    @Override
    public <T> Exceptional<ProxyManager<T>> manager(final T instance) {
        if (instance instanceof Proxy proxy) {
            return Exceptional.of(proxy.manager());
        }
        return null;
    }

    @Override
    public <D, T extends D> Exceptional<D> delegate(final TypeContext<D> type, final T instance) {
        if (instance instanceof Proxy proxy) {
            final ProxyManager manager = proxy.manager();
            return manager.delegate(type.type());
        }
        return Exceptional.empty();
    }

    @Override
    public <T> StateAwareProxyFactory<T, ?> factory(final TypeContext<T> type) {
        return new JavassistProxyFactory<>(type.type(), this.applicationManager().applicationContext());
    }

    @Override
    public <T> Class<T> unproxy(final T instance) {
        for (final ProxyLookup lookup : this.proxyLookups) {
            if (lookup.isProxy(instance)) return lookup.unproxy(instance);
        }
        return instance != null ? (Class<T>) instance.getClass() : null;
    }

    @Override
    public boolean isProxy(final Object instance) {
        return this.proxyLookups.stream().anyMatch(lookup -> lookup.isProxy(instance));
    }

    @Override
    public boolean isProxy(final Class<?> candidate) {
        return this.proxyLookups.stream().anyMatch(lookup -> lookup.isProxy(candidate));
    }

    public void registerProxyLookup(final ProxyLookup proxyLookup) {
        this.proxyLookups.add(proxyLookup);
    }
}
