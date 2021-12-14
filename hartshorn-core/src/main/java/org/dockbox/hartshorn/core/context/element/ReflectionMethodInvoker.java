package org.dockbox.hartshorn.core.context.element;

import org.dockbox.hartshorn.core.domain.Exceptional;

public class ReflectionMethodInvoker<T, P> implements MethodInvoker<T, P> {

    @Override
    public Exceptional<T> invoke(final MethodContext<T, P> method, final P instance, final Object[] args) {
        final Exceptional<T> result = Exceptional.of(() -> (T) method.method().invoke(instance, args));
        if (result.caught()) {
            Throwable cause = result.error();
            if (result.error().getCause() != null) cause = result.error().getCause();
            return Exceptional.of(result.orNull(), cause);
        }
        return result;
    }
}
