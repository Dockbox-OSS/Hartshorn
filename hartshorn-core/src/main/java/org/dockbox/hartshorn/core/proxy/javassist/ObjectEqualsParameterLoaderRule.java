package org.dockbox.hartshorn.core.proxy.javassist;

import org.dockbox.hartshorn.core.context.ParameterLoaderContext;
import org.dockbox.hartshorn.core.context.element.ParameterContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.proxy.ProxyHandler;
import org.dockbox.hartshorn.core.services.parameter.ParameterLoaderRule;

public class ObjectEqualsParameterLoaderRule implements ParameterLoaderRule<ParameterLoaderContext> {
    @Override
    public boolean accepts(final ParameterContext<?> parameter, final int index, final ParameterLoaderContext context, final Object... args) {
        return parameter.declaredBy().parent().is(Object.class) && parameter.declaredBy().name().equals("equals");
    }

    @Override
    public <T> Exceptional<T> load(final ParameterContext<T> parameter, final int index, final ParameterLoaderContext context, final Object... args) {
        final Object argument = args[index];
        final Exceptional<ProxyHandler<Object>> handler = context.applicationContext().environment().manager().handler(argument);
        return handler.flatMap(ProxyHandler::instance).orElse(() -> argument).map(a -> (T) a);
    }
}
