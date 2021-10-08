package org.dockbox.hartshorn.web.error;

import org.dockbox.hartshorn.api.exceptions.ApplicationException;

public interface ErrorServlet {
    void handle(RequestError error) throws ApplicationException;
}
