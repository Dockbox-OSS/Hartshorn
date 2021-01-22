package org.dockbox.selene.core.proxy;

import org.dockbox.selene.core.exceptions.global.UncheckedSeleneException;

/**
 * The exception to be thrown by methods annotated with {@link org.dockbox.selene.core.annotations.proxy.Proxy.Target}
 * to indicate a proxy delegation should be cancelled.
 */
public class CancelProxyException extends UncheckedSeleneException {

    public CancelProxyException() {
        super("");
    }
}
