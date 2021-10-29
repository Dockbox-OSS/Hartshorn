package org.dockbox.hartshorn.core.services;

import org.dockbox.hartshorn.core.HartshornUtils;
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
import java.util.Map;

public abstract class ProxyDelegationModifier<P, A extends Annotation> extends ServiceMethodModifier<A> {

    private final Map<Object, Attribute<?>[]> attributeCache = HartshornUtils.emptyConcurrentMap();
    private final Map<Object, P> backedImplementations = HartshornUtils.emptyConcurrentMap();

    protected abstract Class<P> parentTarget();

    @Override
    protected <T> boolean modifies(final ApplicationContext context, final TypeContext<T> type, @Nullable final T instance, final Attribute<?>... properties) {
        final boolean modifies = type.childOf(this.parentTarget());
        if (modifies) {
            this.attributeCache.put(instance, properties);
        }
        return modifies;
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
            final P concrete = this.backedImplementations.computeIfAbsent(methodContext.instance(), instance -> {
                final Attribute<?>[] attributes = this.attributeCache.get(methodContext.instance());
                return this.concreteDelegator(context, (TypeContext<? extends P>) methodContext.type(), attributes);
            });
            return (instance, args, proxyContext) -> parent.invoke(concrete, args).map((r -> (R) r)).orElse(() -> defaultValue).orNull();
        }
        else {
            context.log().error("Attempted to delegate method " + method.qualifiedName() + " but it was not find on the indicated parent " + parentContext.qualifiedName());
            return null;
        }
    }

    protected P concreteDelegator(final ApplicationContext context, final TypeContext<? extends P> parent, final Attribute<?>... attributes) {
        return context.get(this.parentTarget(), attributes);
    }
}
