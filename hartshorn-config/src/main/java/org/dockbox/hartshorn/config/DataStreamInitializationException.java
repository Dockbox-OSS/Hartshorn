package org.dockbox.hartshorn.config;

import org.dockbox.hartshorn.util.ApplicationException;

public class DataStreamInitializationException extends ApplicationException {

    public DataStreamInitializationException(String message) {
        super(message);
    }

    public DataStreamInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
