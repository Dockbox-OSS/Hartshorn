package org.dockbox.hartshorn.inject;

import org.dockbox.hartshorn.util.ApplicationException;

public class ComponentConfigurationException extends ApplicationException {

    public ComponentConfigurationException(final String message) {
        super(message);
    }

    public ComponentConfigurationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
