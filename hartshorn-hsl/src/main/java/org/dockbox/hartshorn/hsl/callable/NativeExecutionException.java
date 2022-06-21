package org.dockbox.hartshorn.hsl.callable;

import org.dockbox.hartshorn.util.ApplicationException;

public class NativeExecutionException extends ApplicationException {
    public NativeExecutionException(final String message, final Throwable e) {
        super(message, e);
    }

    public NativeExecutionException(final String message) {
        super(message);
    }
}
