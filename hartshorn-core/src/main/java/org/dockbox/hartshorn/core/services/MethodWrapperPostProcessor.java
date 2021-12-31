package org.dockbox.hartshorn.core.services;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.core.Key;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.proxy.MethodWrapper;
import org.dockbox.hartshorn.core.proxy.MethodWrapperFunction;
import org.dockbox.hartshorn.core.proxy.MethodWrapperImpl;
import org.dockbox.hartshorn.core.proxy.ProxyHandler;
import org.dockbox.hartshorn.core.proxy.WrappingPhase;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.stream.Collectors;

public abstract class MethodWrapperPostProcessor<A extends Annotation> extends FunctionalComponentPostProcessor<A>{

    public abstract WrappingPhase phase();

    public abstract <T> boolean wraps(final ApplicationContext context, final MethodContext<?, T> method, final Key<T> key, @Nullable final T instance);

    protected <T> Collection<MethodContext<?, T>> modifiableMethods(final ApplicationContext context, final Key<T> key, @Nullable final T instance) {
        return key.type().methods().stream()
                .filter(method -> this.wraps(context, method, key, instance))
                .collect(Collectors.toList());
    }

    @Override
    public <T> T process(final ApplicationContext context, final Key<T> key, @Nullable final T instance) {
        final TypeContext<T> type = key.type();
        final Collection<MethodContext<?, T>> methods = this.modifiableMethods(context, key, instance);

        // Will reuse existing handler of proxy
        final ProxyHandler<T> handler = context.environment().manager().handler(type, instance);
        final WrappingPhase phase = this.phase();

        for (final MethodContext<?, T> method : methods) {
            final MethodWrapperFunction<T> wrap = this.wrap(context, method, key, instance);
            final MethodWrapper<T> methodWrapper = new MethodWrapperImpl<>(phase, method, wrap);
            handler.wrapper(methodWrapper);
        }

        return instance;
    }

    public abstract <T> MethodWrapperFunction<T> wrap(final ApplicationContext context, final MethodContext<?, T> method, final Key<T> key, @Nullable final T instance);
}
