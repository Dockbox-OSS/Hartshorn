package org.dockbox.hartshorn.web.processing.rules;

import org.dockbox.hartshorn.core.context.element.ParameterContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.services.parameter.ParameterLoaderRule;
import org.dockbox.hartshorn.web.processing.HttpRequestParameterLoaderContext;

public class ServletRequestParameterRule implements ParameterLoaderRule<HttpRequestParameterLoaderContext> {
    @Override
    public boolean accepts(final ParameterContext<?> parameter, final HttpRequestParameterLoaderContext context, final Object... args) {
        return TypeContext.of(context.request()).childOf(parameter.type());
    }

    @Override
    public <T> Exceptional<T> load(final ParameterContext<T> parameter, final HttpRequestParameterLoaderContext context, final Object... args) {
        return Exceptional.of((T) context.request());
    }
}
