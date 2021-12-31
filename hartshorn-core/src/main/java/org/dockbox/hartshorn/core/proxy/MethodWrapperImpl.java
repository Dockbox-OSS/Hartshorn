package org.dockbox.hartshorn.core.proxy;

import org.dockbox.hartshorn.core.context.element.MethodContext;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class MethodWrapperImpl<T> implements MethodWrapper<T> {

    @Getter final WrappingPhase phase;
    @Getter final MethodContext<?, T> method;

    final MethodWrapperFunction<T> function;

    @Override
    public void accept(final MethodContext<?, T> method, final T instance, final Object[] args, final ProxyContext context) {
        this.function.accept(method, instance, args, context);
    }
}
