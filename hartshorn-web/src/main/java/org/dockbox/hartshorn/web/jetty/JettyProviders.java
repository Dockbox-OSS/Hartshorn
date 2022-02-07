package org.dockbox.hartshorn.web.jetty;

import org.dockbox.hartshorn.core.annotations.inject.Provider;
import org.dockbox.hartshorn.core.annotations.stereotype.Service;
import org.dockbox.hartshorn.web.HttpWebServer;
import org.dockbox.hartshorn.web.annotations.UseHttpServer;
import org.dockbox.hartshorn.web.servlet.DirectoryServlet;

@Service(activators = UseHttpServer.class, requires = "org.eclipse.jetty.server.Server")
public class JettyProviders {

    @Provider
    public DirectoryServlet directoryServlet() {
        return new JettyDirectoryServlet();
    }

    @Provider
    public HttpWebServer httpWebServer(final JettyResourceService resourceService) {
        return new JettyHttpWebServer(resourceService);
    }
}
