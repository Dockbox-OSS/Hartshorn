package org.dockbox.hartshorn.core.proxy.javassist;

import org.dockbox.hartshorn.core.annotations.Unproxy;
import org.dockbox.hartshorn.core.context.ParameterLoaderContext;
import org.dockbox.hartshorn.core.context.element.ParameterContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.proxy.ProxyHandler;
import org.dockbox.hartshorn.core.services.parameter.ParameterLoaderRule;

public class UnproxyParameterLoaderRule implements ParameterLoaderRule<ParameterLoaderContext> {
    @Override
    public boolean accepts(final ParameterContext<?> parameter, final int index, final ParameterLoaderContext context, final Object... args) {
        return parameter.annotation(Unproxy.class).present() || parameter.declaredBy().annotation(Unproxy.class).present();
    }

    @Override
    public <T> Exceptional<T> load(final ParameterContext<T> parameter, final int index, final ParameterLoaderContext context, final Object... args) {
        final Object argument = args[index];
        final Exceptional<ProxyHandler<Object>> handler = context.applicationContext().environment().manager().handler(argument);
        return handler.flatMap(ProxyHandler::instance).orElse(() -> {
            final Unproxy unproxy = parameter.annotation(Unproxy.class).orElse(() -> parameter.declaredBy().annotation(Unproxy.class).orNull()).get();
            if (unproxy.fallbackToProxy()) return argument;
            else return null;
        }).map(a -> (T) a);
    }
}
