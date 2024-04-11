package org.dockbox.hartshorn.component;

import org.dockbox.hartshorn.util.ApplicationException;

public class FailedPostConstructionException extends ApplicationException {

    public FailedPostConstructionException(String message) {
        super(message);
    }

    public FailedPostConstructionException(String message, Throwable cause) {
        super(message, cause);
    }

    public FailedPostConstructionException(Throwable cause) {
        super(cause);
    }
}
