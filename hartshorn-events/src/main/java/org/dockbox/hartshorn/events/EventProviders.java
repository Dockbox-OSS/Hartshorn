package org.dockbox.hartshorn.events;

import org.dockbox.hartshorn.core.annotations.inject.Provider;
import org.dockbox.hartshorn.core.annotations.stereotype.Service;
import org.dockbox.hartshorn.core.services.parameter.ParameterLoader;
import org.dockbox.hartshorn.events.annotations.UseEvents;
import org.dockbox.hartshorn.events.handle.EventParameterLoader;

import javax.inject.Singleton;

@Service(activators = UseEvents.class)
public class EventProviders {

    @Singleton
    @Provider
    public EventBus eventBus() {
        return new EventBusImpl();
    }

    @Provider("event_loader")
    public ParameterLoader eventParameterLoader() {
        return new EventParameterLoader();
    }
}
