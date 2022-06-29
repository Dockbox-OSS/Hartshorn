package org.dockbox.hartshorn.hsl.callable;

import org.dockbox.hartshorn.util.ApplicationException;

/**
 * The exception thrown by module loaders accessing native functions. This exception is thrown
 * when a native function is called that is not supported by the module, or is not accessible
 * to the active runtime.
 *
 * @author Guus Lieben
 * @since 22.4
 */
public class NativeExecutionException extends ApplicationException {
    public NativeExecutionException(final String message, final Throwable e) {
        super(message, e);
    }

    public NativeExecutionException(final String message) {
        super(message);
    }
}
