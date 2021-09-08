/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.proxy;

import org.dockbox.hartshorn.boot.Hartshorn;
import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.exceptions.Except;
import org.dockbox.hartshorn.di.InjectionPoint;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.di.context.element.MethodContext;
import org.dockbox.hartshorn.di.context.element.ParameterContext;
import org.dockbox.hartshorn.di.context.element.TypeContext;
import org.dockbox.hartshorn.proxy.annotations.Instance;
import org.dockbox.hartshorn.proxy.annotations.Proxy;
import org.dockbox.hartshorn.proxy.annotations.Proxy.Target;
import org.dockbox.hartshorn.proxy.exception.CancelProxyException;
import org.dockbox.hartshorn.proxy.handle.ProxyHandler;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class ProxyableBootstrap {

    private ProxyableBootstrap() {}

    static void boostrapDelegates(final ApplicationContext context) {
        Hartshorn.log().info("Scanning for proxy types in all context prefixes");
        context.environment().types(Proxy.class).forEach(proxy -> {
            Hartshorn.log().info("Processing global proxy " + proxy.qualifiedName());
            if (proxy.isAbstract()) {
                Hartshorn.log().warn("Proxy source cannot be abstract [" + proxy.qualifiedName() + "]");
                return;
            }

            final Proxy delegationInfo = proxy.annotation(Proxy.class).get();
            if (TypeContext.of(delegationInfo.value()).annotation(Proxy.class).present()) {
                Hartshorn.log().warn("Proxy target cannot be another delegate [" + proxy.qualifiedName() + "]");
                return;
            }

            ProxyableBootstrap.delegateMethods(context, proxy);
        });
    }

    private static <T> void delegateMethods(final ApplicationContext context, final TypeContext<T> proxyClass) {
        final List<? extends MethodContext<?, T>> targets = proxyClass.flatMethods(Target.class);

        final Proxy proxy = proxyClass.annotation(Proxy.class).get();
        targets.forEach(target -> ProxyableBootstrap.delegateMethod(context, proxyClass, TypeContext.of(proxy.value()), target));
    }

    private static <T, C> void delegateMethod(final ApplicationContext context, final TypeContext<T> proxyClass, final TypeContext<C> proxyTargetClass, final MethodContext<?, T> source) {
        Hartshorn.log().info("Processing " + proxyClass.name() + "." + source.name());
        if (source.isAbstract()) {
            Hartshorn.log().warn("Proxy method cannot be abstract [" + source.name() + "]");
            return;
        }

        String methodName = source.name();

        // Used only for method lookup, parameters decorated with @Instance are injected and thus not
        // present on the
        // target method.
        final List<TypeContext<?>> arguments = source.parameters().stream()
                .filter(arg -> arg.annotation(Instance.class).absent())
                .map(ParameterContext::type)
                .collect(Collectors.toList());

        // By default the name of the proxy method is used, however it's possible two methods proxy
        // separate stages of
        // the same method, so the method names are different. The @Target annotation allows setting the
        // method name
        // manually.
        final Target target = source.annotation(Target.class).get();
        if (!target.method().isEmpty()) methodName = target.method();
        final Exceptional<MethodContext<?, C>> targetMethod = proxyTargetClass.method(methodName, arguments);
        if (targetMethod.absent()) {
            Hartshorn.log().warn("Proxy target does not have declared method '" + methodName + "(" + Arrays.toString(arguments.toArray()) + ") [" + proxyClass.qualifiedName() + "]");
            return;
        }

        final MethodContext<?, C> methodContext = targetMethod.get();

        // If the target method has a return type other than `void`, and the proxy wants to overwrite
        // the return
        // value, we need to ensure the return value of the proxy method is assignable from the return
        // type of the
        // target.
        if (!source.returnType().isVoid() && target.overwrite()) {
            if (!methodContext.returnType().childOf(source.returnType())) {
                Hartshorn.log().warn("Return type for '" + source.name() + "' is not assignable from '" + methodContext.returnType() + "' [" + proxyClass.qualifiedName() + "]");
                return;
            }
        }

        final ProxyAttribute<C, ?> property = ProxyAttribute.of(proxyTargetClass, methodContext, (instance, args, proxyContext) -> {
            final Object[] invokingArgs = ProxyableBootstrap.prepareArguments(context, source, args, instance);
            try {
                return source.invoke(context.get(proxyClass), invokingArgs).orNull();
            }
            catch (final CancelProxyException e) {
                proxyContext.holder().cancelled(true);
            }
            catch (final Throwable e) {
                Except.handle(e);
            }
            //noinspection ReturnOfNull
            return null;
        });
        property.phase(target.at());
        property.priority(target.priority());
        property.overwriteResult(target.overwrite());
        final InjectionPoint<C> point = InjectionPoint.of(proxyTargetClass, instance -> {
            try {
                final ProxyHandler<C> handler = new ProxyHandler<>(instance);
                handler.delegate(property);
                return handler.proxy();
            }
            catch (final Throwable t) {
                Except.handle(t);
            }
            return instance;
        });
        context.add(point);
    }

    private static Object[] prepareArguments(final ApplicationContext context, final MethodContext<?, ?> method, final Object[] args, final Object instance) {
        final List<Object> arguments = HartshornUtils.emptyList();
        if (method.parameterCount() >= 1 && method.parameters().get(0).annotation(Instance.class).present()) {
            arguments.add(instance);
        }
        arguments.addAll(Arrays.asList(args));
        return arguments.toArray();
    }
}
