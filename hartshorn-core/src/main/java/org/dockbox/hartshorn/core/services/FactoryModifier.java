package org.dockbox.hartshorn.core.services;

import org.dockbox.hartshorn.core.HartshornUtils;
import org.dockbox.hartshorn.core.Key;
import org.dockbox.hartshorn.core.annotations.Factory;
import org.dockbox.hartshorn.core.annotations.service.Service;
import org.dockbox.hartshorn.core.binding.Bindings;
import org.dockbox.hartshorn.core.binding.ContextDrivenProvider;
import org.dockbox.hartshorn.core.binding.Provider;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.MethodProxyContext;
import org.dockbox.hartshorn.core.context.element.ConstructorContext;
import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.proxy.ProxyFunction;

import java.util.LinkedList;
import java.util.Set;

public class FactoryModifier extends ServiceAnnotatedMethodModifier<Factory, Service> {

    @Override
    public Class<Service> activator() {
        return Service.class;
    }

    @Override
    public Class<Factory> annotation() {
        return Factory.class;
    }

    @Override
    public <T> boolean preconditions(final ApplicationContext context, final MethodProxyContext<T> methodContext) {
        return !methodContext.method().returnType().isVoid();
    }

    @Override
    public <T, R> ProxyFunction<T, R> process(final ApplicationContext context, final MethodProxyContext<T> methodContext) {
        if (methodContext.method().isAbstract()) {
            final MethodContext<?, T> method = methodContext.method();
            final Factory annotation = method.annotation(Factory.class).get();
            Key<?> key = Key.of(method.returnType());
            if (!"".equals(annotation.value())) key = Key.of(method.returnType(), Bindings.named(annotation.value()));

            final Set<TypeContext<?>> types = HartshornUtils.emptySet();
            for (final Provider<?> provider : context.hierarchy(key).providers()) {
                if (provider instanceof ContextDrivenProvider contextDrivenProvider) {
                    types.add(contextDrivenProvider.context());
                }
            }
            if (types.isEmpty()) throw new IllegalStateException("No provider found for " + key);

            ConstructorContext<?> matching = null;
            candidates:
            for (final TypeContext<?> typeContext : types) {
                for (final ConstructorContext<?> constructor : typeContext.boundConstructors()) {
                    final LinkedList<TypeContext<?>> constructorParemeters = constructor.parameterTypes();
                    final LinkedList<TypeContext<?>> methodParameters = method.parameterTypes();
                    if (methodParameters.equals(constructorParemeters)) {
                        matching = constructor;
                        break candidates;
                    }
                }
            }

            if (matching == null) throw new IllegalStateException("No matching bound constructor found for " + key + " with parameters: " + method.parameterTypes());

            final ConstructorContext<?> finalMatching = matching;
            return (instance, args, proxyContext) -> (R) finalMatching.createInstance(args).orNull();
        }
        else {
            return (instance, args, proxyContext) -> (R) methodContext.method().invoke(instance, args);
        }
    }

    @Override
    public ServiceOrder order() {
        return ServiceOrder.LAST;
    }
}
