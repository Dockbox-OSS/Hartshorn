package org.dockbox.hartshorn.core.services.parameter;

import org.dockbox.hartshorn.core.context.ParameterLoaderContext;
import org.dockbox.hartshorn.core.context.element.ParameterContext;
import org.dockbox.hartshorn.core.domain.Exceptional;

public interface ParameterLoaderRule<C extends ParameterLoaderContext> {
    boolean accepts(ParameterContext<?> parameter, C context, Object... args);
    <T> Exceptional<T> load(ParameterContext<T> parameter, C context, Object... args);
}
