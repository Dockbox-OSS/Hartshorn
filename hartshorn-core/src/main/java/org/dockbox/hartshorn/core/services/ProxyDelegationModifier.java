package org.dockbox.hartshorn.core.services;

import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.MethodProxyContext;
import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.properties.Attribute;
import org.dockbox.hartshorn.core.proxy.ProxyFunction;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.util.Collection;

public abstract class ProxyDelegationModifier<P, A extends Annotation> extends ServiceMethodModifier<A> {

    protected abstract Class<P> parentTarget();

    @Override
    protected <T> boolean modifies(final ApplicationContext context, final TypeContext<T> type, @Nullable final T instance, final Attribute<?>... properties) {
        return type.childOf(this.parentTarget());
    }

    @Override
    protected <T> Collection<MethodContext<?, T>> modifiableMethods(final TypeContext<T> type) {
        return type.methods().stream().filter(method -> method.parent().is(this.parentTarget())).toList();
    }

    @Override
    public <T> boolean preconditions(final ApplicationContext context, final MethodProxyContext<T> methodContext) {
        return methodContext.method().parent().is(this.parentTarget());
    }

    @Override
    public <T, R> ProxyFunction<T, R> process(final ApplicationContext context, final MethodProxyContext<T> methodContext) {
        final TypeContext<P> parentContext = TypeContext.of(this.parentTarget());
        final MethodContext<?, T> method = methodContext.method();
        final Exceptional<MethodContext<?, P>> parentMethod = parentContext.method(method.name(), method.parameterTypes());
        if (parentMethod.present()) {
            final MethodContext<?, P> parent = parentMethod.get();
            final R defaultValue = (R) parent.returnType().defaultOrNull();
            return (instance, args, proxyContext) -> parent.invoke(context.get(this.parentTarget()), args).map((r -> (R) r)).orElse(() -> defaultValue).get();
        }
        else {
            context.log().error("Attempted to delegate method " + method.qualifiedName() + " but it was not find on the indicated parent " + parentContext.qualifiedName());
            return null;
        }
    }
}
