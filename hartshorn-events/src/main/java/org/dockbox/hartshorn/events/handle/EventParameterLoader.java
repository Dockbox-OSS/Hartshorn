package org.dockbox.hartshorn.events.handle;

import org.dockbox.hartshorn.core.annotations.inject.Binds;
import org.dockbox.hartshorn.core.services.parameter.ParameterLoader;
import org.dockbox.hartshorn.core.services.parameter.RuleBasedParameterLoader;

import javax.inject.Named;

@Binds(value = ParameterLoader.class, named = @Named("event_loader"))
public class EventParameterLoader extends RuleBasedParameterLoader<EventParameterLoaderContext> {
    public EventParameterLoader() {
        this.add(new EventParameterRule());
    }
}
