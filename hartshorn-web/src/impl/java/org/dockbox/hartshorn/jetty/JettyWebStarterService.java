package org.dockbox.hartshorn.jetty;

import org.dockbox.hartshorn.api.exceptions.ApplicationException;
import org.dockbox.hartshorn.boot.ServerState.Started;
import org.dockbox.hartshorn.di.annotations.service.Service;
import org.dockbox.hartshorn.events.EngineChangedState;
import org.dockbox.hartshorn.events.annotations.Listener;
import org.dockbox.hartshorn.web.annotations.UseWebStarter;

@Service(activators = UseWebStarter.class)
public class JettyWebStarterService {

    @Listener
    public void on(EngineChangedState<Started> event) throws ApplicationException {
        // TODO GLieben #417: Replace placeholder with @Value configuration
        new JettyWebStarter().start(8080);
    }
}