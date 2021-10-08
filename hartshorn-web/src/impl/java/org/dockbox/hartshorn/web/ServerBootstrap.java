package org.dockbox.hartshorn.web;

import org.dockbox.hartshorn.api.exceptions.ApplicationException;
import org.dockbox.hartshorn.boot.ServerState.Started;
import org.dockbox.hartshorn.boot.annotations.UseBootstrap;
import org.dockbox.hartshorn.config.annotations.Value;
import org.dockbox.hartshorn.di.annotations.service.Service;
import org.dockbox.hartshorn.events.EngineChangedState;
import org.dockbox.hartshorn.events.annotations.Listener;

@Service(activators = UseBootstrap.class)
public class ServerBootstrap {

    @Value(value = "hartshorn.web.port", or = "8080")
    private int port;

    @Listener
    public void on(final EngineChangedState<Started> event) throws ApplicationException {
        final WebStarter starter = event.applicationContext().get(WebStarter.class);
        final ControllerContext controllerContext = event.applicationContext().first(ControllerContext.class).get();
        for (final RequestHandlerContext context : controllerContext.contexts()) {
            starter.register(context);
        }
        starter.start(this.port);
    }

}
