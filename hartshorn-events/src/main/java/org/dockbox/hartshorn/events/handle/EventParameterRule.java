package org.dockbox.hartshorn.events.handle;

import org.dockbox.hartshorn.core.context.element.ParameterContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.services.parameter.ParameterLoaderRule;

public class EventParameterRule implements ParameterLoaderRule<EventParameterLoaderContext> {
    @Override
    public boolean accepts(final ParameterContext<?> parameter, int index, final EventParameterLoaderContext context, final Object... args) {
        return TypeContext.of(context.event()).childOf(parameter.type());
    }

    @Override
    public <T> Exceptional<T> load(final ParameterContext<T> parameter, int index, final EventParameterLoaderContext context, final Object... args) {
        return Exceptional.of((T) context.event());
    }
}
