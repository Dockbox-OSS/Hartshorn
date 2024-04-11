package org.dockbox.hartshorn.component;

import org.dockbox.hartshorn.util.ApplicationRuntimeException;

public class ComponentLookupException extends ApplicationRuntimeException {

    public ComponentLookupException(String message) {
        super(message);
    }

    public ComponentLookupException(String message, Throwable cause) {
        super(message, cause);
    }

    public ComponentLookupException(Throwable cause) {
        super(cause);
    }
}
