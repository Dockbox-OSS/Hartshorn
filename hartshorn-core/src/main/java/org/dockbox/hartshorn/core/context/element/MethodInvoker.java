package org.dockbox.hartshorn.core.context.element;

import org.dockbox.hartshorn.core.domain.Exceptional;

@FunctionalInterface
public interface MethodInvoker<T, P> {
    Exceptional<T> invoke(MethodContext<T, P> method, P instance, Object[] args);
}
