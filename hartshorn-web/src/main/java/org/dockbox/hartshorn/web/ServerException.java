package org.dockbox.hartshorn.web;

import org.dockbox.hartshorn.util.ApplicationException;

public class ServerException extends ApplicationException {

    public ServerException(String message) {
        super(message);
    }

    public ServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServerException(Throwable cause) {
        super(cause);
    }
}
