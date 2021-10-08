package org.dockbox.hartshorn.jetty.error;

import org.dockbox.hartshorn.di.annotations.inject.Binds;
import org.dockbox.hartshorn.web.error.ErrorServlet;
import org.dockbox.hartshorn.web.error.RequestError;

@Binds(ErrorServlet.class)
public class JettyErrorServlet implements ErrorServlet {
    @Override
    public void handle(RequestError error) {
        error.yieldDefaults(true);
    }
}
