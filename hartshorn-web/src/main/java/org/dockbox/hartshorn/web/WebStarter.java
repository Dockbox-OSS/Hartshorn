package org.dockbox.hartshorn.web;

import org.dockbox.hartshorn.api.exceptions.ApplicationException;

public interface WebStarter {
    void start(int port) throws ApplicationException;
    void register(RequestHandlerContext context);
}
