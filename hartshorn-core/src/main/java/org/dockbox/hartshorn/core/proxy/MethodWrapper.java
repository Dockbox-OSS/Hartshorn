package org.dockbox.hartshorn.core.proxy;

import org.dockbox.hartshorn.core.context.element.MethodContext;

public interface MethodWrapper<T> {
    void acceptBefore(MethodContext<?, T> method, T instance, Object[] args);

    void acceptAfter(MethodContext<?, T> method, T instance, Object[] args);

    void acceptError(MethodContext<?, T> method, T instance, Object[] args, Throwable error);

    static <T> MethodWrapper<T> of(final ProxyCallback<T> before, final ProxyCallback<T> after, final ProxyCallback<T> afterThrowing) {
        return new MethodWrapper<>() {
            @Override
            public void acceptBefore(final MethodContext<?, T> method, final T instance, final Object[] args) {
                if (before != null) before.accept(method, instance, args);
            }

            @Override
            public void acceptAfter(final MethodContext<?, T> method, final T instance, final Object[] args) {
                if (after != null) after.accept(method, instance, args);
            }

            @Override
            public void acceptError(final MethodContext<?, T> method, final T instance, final Object[] args, final Throwable error) {
                if (afterThrowing != null) afterThrowing.accept(method, instance, args);
            }
        };
    }
}
