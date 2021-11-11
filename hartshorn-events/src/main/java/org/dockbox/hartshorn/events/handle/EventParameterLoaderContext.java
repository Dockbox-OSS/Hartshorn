package org.dockbox.hartshorn.events.handle;

import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.ParameterLoaderContext;
import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.events.parents.Event;

import lombok.Getter;

@Getter
public class EventParameterLoaderContext extends ParameterLoaderContext {

    private final Event event;

    public EventParameterLoaderContext(final MethodContext<?, ?> method, final TypeContext<?> type, final Object instance,
                                       final ApplicationContext applicationContext, final Event event) {
        super(method, type, instance, applicationContext);
        this.event = event;
    }
}
