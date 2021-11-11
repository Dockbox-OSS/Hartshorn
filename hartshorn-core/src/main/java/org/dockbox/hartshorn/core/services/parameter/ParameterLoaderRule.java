package org.dockbox.hartshorn.core.services.parameter;

import org.dockbox.hartshorn.core.context.ParameterLoaderContext;
import org.dockbox.hartshorn.core.context.element.ParameterContext;
import org.dockbox.hartshorn.core.domain.Exceptional;

public interface ParameterLoaderRule<C extends ParameterLoaderContext> {
    boolean accepts(ParameterContext<?> parameter, int index, C context, Object... args);
    <T> Exceptional<T> load(ParameterContext<T> parameter, int index, C context, Object... args);
}
