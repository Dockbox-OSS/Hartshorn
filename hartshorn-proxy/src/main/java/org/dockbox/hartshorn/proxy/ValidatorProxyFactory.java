package org.dockbox.hartshorn.proxy;

import org.dockbox.hartshorn.proxy.constraint.ProxyValidator;

/**
 * A {@link ProxyFactory} that will first validate the type before creating the proxy. The
 * validation may be performed during initialization or during the creation of the proxy,
 * depending on the implementation.
 *
 * @param <T> the type of the proxy
 */
public interface ValidatorProxyFactory<T> extends ProxyFactory<T> {

    /**
     * Returns the validator that is used to validate the type before creating the proxy.
     *
     * @return the validator that is used to validate the type before creating the proxy
     */
    ProxyValidator validator();
}
