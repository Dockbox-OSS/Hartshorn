package org.dockbox.hartshorn.persistence.hibernate;

import org.dockbox.hartshorn.core.proxy.ProxyLookup;
import org.hibernate.proxy.HibernateProxy;

public class HibernateProxyLookup implements ProxyLookup {

    @Override
    public <T> Class<T> unproxy(final T instance) {
        if (instance instanceof HibernateProxy hibernateProxy) {
            return (Class<T>) hibernateProxy.getHibernateLazyInitializer().getPersistentClass();
        }
        return instance != null ? (Class<T>) instance.getClass() : null;
    }

    @Override
    public boolean isProxy(final Object instance) {
        return instance instanceof HibernateProxy;
    }

    @Override
    public boolean isProxy(final Class<?> candidate) {
        return HibernateProxy.class.isAssignableFrom(candidate);
    }
}
