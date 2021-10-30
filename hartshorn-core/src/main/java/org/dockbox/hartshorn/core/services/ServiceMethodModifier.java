package org.dockbox.hartshorn.core.services;

import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.MethodProxyContext;
import org.dockbox.hartshorn.core.context.MethodProxyContextImpl;
import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.exceptions.ProxyMethodBindingException;
import org.dockbox.hartshorn.core.properties.Attribute;
import org.dockbox.hartshorn.core.proxy.ProxyAttribute;
import org.dockbox.hartshorn.core.proxy.ProxyFunction;
import org.dockbox.hartshorn.core.proxy.ProxyHandler;
import org.dockbox.hartshorn.core.proxy.ProxyUtil;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.util.Collection;

public abstract class ServiceMethodModifier<A extends Annotation> extends ServiceModifier<A> {

    @Override
    public <T> T process(final ApplicationContext context, final TypeContext<T> type, @Nullable final T instance, final Attribute<?>... properties) {
        final Collection<MethodContext<?, T>> methods = this.modifiableMethods(type);

        final ProxyHandler<T> handler = ProxyUtil.handler(type, instance);

        for (final MethodContext<?, T> method : methods) {
            final MethodProxyContext<T> ctx = new MethodProxyContextImpl<>(context, instance, type, method, properties, handler);

            if (this.preconditions(context, ctx)) {
                final ProxyFunction<T, Object> function = this.process(context, ctx);
                if (function != null) {
                    final ProxyAttribute<T, ?> property = ProxyAttribute.of(type, method, function);
                    handler.delegate(property);
                }
            }
            else {
                if (this.failOnPrecondition()) {
                    throw new ProxyMethodBindingException(method);
                }
            }
        }

        return Exceptional.of(() -> handler.proxy(instance)).or(instance);
    }

    protected abstract <T> Collection<MethodContext<?, T>> modifiableMethods(TypeContext<T> type);

    public abstract <T> boolean preconditions(ApplicationContext context, MethodProxyContext<T> methodContext);

    public abstract <T, R> ProxyFunction<T, R> process(ApplicationContext context, MethodProxyContext<T> methodContext);

    public boolean failOnPrecondition() {
        return true;
    }

}
