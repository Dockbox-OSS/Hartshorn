package org.dockbox.hartshorn.core.proxy;

import java.util.Objects;

public interface MethodInterceptor<T> {
    Object intercept(MethodInterceptorContext<T> context) throws Throwable;

    default MethodInterceptor<T> andThen(final MethodInterceptor<T> after) {
        Objects.requireNonNull(after);
        return ctx -> {
            final Object previous = this.intercept(ctx);
            return after.intercept(new MethodInterceptorContext(ctx, previous));
        };
    }
}
