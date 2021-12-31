package org.dockbox.hartshorn.core.proxy;

import org.dockbox.hartshorn.core.context.element.MethodContext;

public interface MethodWrapper<T> {

    void accept(MethodContext<?, T> method, T instance, Object[] args, ProxyContext context);

    WrappingPhase phase();

    MethodContext<?, T> method();
}
