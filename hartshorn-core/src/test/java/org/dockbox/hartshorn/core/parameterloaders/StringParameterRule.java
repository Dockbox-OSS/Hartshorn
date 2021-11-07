package org.dockbox.hartshorn.core.parameterloaders;

import org.dockbox.hartshorn.core.context.ParameterLoaderContext;
import org.dockbox.hartshorn.core.context.element.ParameterContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.services.parameter.ParameterLoaderRule;

public class StringParameterRule implements ParameterLoaderRule<ParameterLoaderContext> {
    @Override
    public boolean accepts(final ParameterContext<?> parameter, final ParameterLoaderContext context, final Object... args) {
        return parameter.type().is(String.class);
    }

    @Override
    public <T> Exceptional<T> load(final ParameterContext<T> parameter, final ParameterLoaderContext context, final Object... args) {
        return Exceptional.of((T) "JUnit");
    }
}
