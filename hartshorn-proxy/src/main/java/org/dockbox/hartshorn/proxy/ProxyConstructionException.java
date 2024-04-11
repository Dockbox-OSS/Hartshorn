package org.dockbox.hartshorn.proxy;

import org.dockbox.hartshorn.util.ApplicationException;

/**
 * Thrown when an attempt is made to construct a proxy instance, but the construction fails. This should typically only be
 * thrown by {@link ProxyConstructorFunction}s or {@link ProxyFactory proxy factories} directly.
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public class ProxyConstructionException extends ApplicationException {

    public ProxyConstructionException(String message) {
        super(message);
    }

    public ProxyConstructionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProxyConstructionException(Throwable cause) {
        super(cause);
    }
}
