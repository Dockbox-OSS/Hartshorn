package org.dockbox.hartshorn.web.jetty;

import org.dockbox.hartshorn.inject.annotations.Fuzzy;
import org.dockbox.hartshorn.inject.annotations.PropertyValue;
import org.dockbox.hartshorn.inject.annotations.configuration.Configuration;
import org.dockbox.hartshorn.inject.annotations.configuration.Prototype;
import org.dockbox.hartshorn.inject.annotations.configuration.Singleton;
import org.dockbox.hartshorn.inject.collection.ComponentCollection;
import org.dockbox.hartshorn.launchpad.condition.RequiresActivator;
import org.dockbox.hartshorn.util.configure.Customizer;
import org.dockbox.hartshorn.web.chain.RequestHandlerChain;
import org.dockbox.hartshorn.web.UseWebServer;
import org.dockbox.hartshorn.web.WebServer;
import org.dockbox.hartshorn.web.jetty.route.JettyRequestHandler;
import org.eclipse.jetty.server.Server;

@Configuration
@RequiresActivator(UseWebServer.class)
public class JettyServerConfiguration {

    @Singleton
    public WebServer webServer(Server server) {
        return new JettyWebServer(server);
    }

    @Prototype
    public Server jettyServer(
            RequestHandlerChain chain,
            @Fuzzy ComponentCollection<Customizer<Server>> customizers,
            @PropertyValue(name = "hartshorn.web.port", defaultValue = "8080") int port
    ) {
        Server server = new Server(port);
        server.setHandler(new JettyRequestHandler(chain));
        customizers.forEach(customizer -> customizer.configure(server));
        return server;
    }
}
