package org.dockbox.hartshorn.inject;

import org.dockbox.hartshorn.util.ApplicationException;

public class DependencyResolutionException extends ApplicationException {

    public DependencyResolutionException(String message) {
        super(message);
    }

    public DependencyResolutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public DependencyResolutionException(Throwable cause) {
        super(cause);
    }
}
