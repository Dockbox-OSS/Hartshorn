package org.dockbox.selene.core.delegate;

import org.dockbox.selene.core.exceptions.global.UncheckedSeleneException;

/**
 * The exception to be thrown by methods annotated with {@link org.dockbox.selene.core.annotations.delegate.Proxy.Target}
 * to indicate a proxy delegation should be cancelled.
 */
public class CancelDelegateException extends UncheckedSeleneException {

    public CancelDelegateException() {
        super("");
    }
}
