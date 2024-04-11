package org.dockbox.hartshorn.proxy.advice.intercept;

import java.util.concurrent.Callable;
import org.dockbox.hartshorn.util.ApplicationException;

/**
 * Thrown when an unexpected exception occurs during the execution of a {@link CustomInvocation}. This is primarily
 * a wrapper exception when translating {@link CustomInvocation#call(Object...)} to a {@link Callable} through
 * {@link CustomInvocation#toCallable(Object...)}.
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public class CustomInvocationException extends ApplicationException {

    public CustomInvocationException(String message, Throwable cause) {
        super(message, cause);
    }
}
