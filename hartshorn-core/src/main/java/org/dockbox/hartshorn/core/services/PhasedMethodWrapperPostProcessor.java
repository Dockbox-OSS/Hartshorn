package org.dockbox.hartshorn.core.services;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.core.Key;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.proxy.MethodWrapper;
import org.dockbox.hartshorn.core.proxy.MethodWrapperFunction;
import org.dockbox.hartshorn.core.proxy.MethodWrapperImpl;
import org.dockbox.hartshorn.core.proxy.ProxyHandler;
import org.dockbox.hartshorn.core.proxy.WrappingPhase;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.stream.Collectors;

public abstract class PhasedMethodWrapperPostProcessor<A extends Annotation> extends FunctionalComponentPostProcessor<A> {

    @Override
    public <T> T process(final ApplicationContext context, final Key<T> key, @Nullable final T instance) {
        final Collection<MethodContext<?, T>> methods = this.modifiableMethods(context, key, instance);

        // Will reuse existing handler of proxy
        final ProxyHandler<T> handler = context.environment().manager().handler(key.type(), instance);

        for (final MethodContext<?, T> method : methods) {
            final MethodWrapperFunction<T> before = this.wrapBefore(context, method, key, instance);
            final MethodWrapperFunction<T> after = this.wrapAfter(context, method, key, instance);
            final MethodWrapperFunction<T> afterThrowing = this.wrapAfterThrowing(context, method, key, instance);

            if (before != null) handler.wrapper(this.wrapper(before, WrappingPhase.BEFORE, method));
            if (after != null) handler.wrapper(this.wrapper(after, WrappingPhase.AFTER, method));
            if (afterThrowing != null) handler.wrapper(this.wrapper(afterThrowing, WrappingPhase.THROWING, method));
        }

        return instance;
    }

    protected <T> MethodWrapper<T> wrapper(final MethodWrapperFunction<T> function, final WrappingPhase phase, final MethodContext<?, T> methodContext) {
        return new MethodWrapperImpl<>(phase, methodContext, function);
    }

    protected <T> Collection<MethodContext<?, T>> modifiableMethods(final ApplicationContext context, final Key<T> key, @Nullable final T instance) {
        return key.type().methods().stream()
                .filter(method -> this.wraps(context, method, key, instance))
                .collect(Collectors.toList());
    }

    public abstract <T> boolean wraps(final ApplicationContext context, final MethodContext<?, T> method, final Key<T> key, @Nullable final T instance);

    public abstract <T> MethodWrapperFunction<T> wrapBefore(final ApplicationContext context, final MethodContext<?, T> method, final Key<T> key, @Nullable final T instance);

    public abstract <T> MethodWrapperFunction<T> wrapAfter(final ApplicationContext context, final MethodContext<?, T> method, final Key<T> key, @Nullable final T instance);

    public abstract <T> MethodWrapperFunction<T> wrapAfterThrowing(final ApplicationContext context, final MethodContext<?, T> method, final Key<T> key, @Nullable final T instance);
}
