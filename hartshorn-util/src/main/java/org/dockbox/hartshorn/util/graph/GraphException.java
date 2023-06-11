package org.dockbox.hartshorn.util.graph;

import org.dockbox.hartshorn.util.ApplicationException;

public class GraphException extends ApplicationException {

    public GraphException(final String message) {
        super(message);
    }

    public GraphException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public GraphException(final Throwable cause) {
        super(cause);
    }
}
