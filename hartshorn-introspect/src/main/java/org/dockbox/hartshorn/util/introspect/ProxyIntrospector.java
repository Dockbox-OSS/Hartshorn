package org.dockbox.hartshorn.util.introspect;

import org.dockbox.hartshorn.util.option.Option;

public interface ProxyIntrospector<T> {

    /**
     * Returns the original type of the proxy. This is the type of the object that is proxied, but is not the proxied
     * type itself.
     *
     * @return the original type of the proxy
     */
    Class<T> targetClass();

    /**
     * Gets the proxied type of the proxy. This is the type of the object that is proxied, but is not the original
     * type of the proxy.
     *
     * @return the proxied type of the proxy
     */
    Class<T> proxyClass();

    /**
     * Returns the proxy instance managed by this manager.
     *
     * @return the proxy instance managed by this manager
     */
    T proxy();

    /**
     * Returns the original instance delegate of the proxy.
     *
     * @return the original instance delegate of the proxy
     */
    Option<T> delegate();
}
