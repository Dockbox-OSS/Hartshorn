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

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.exceptions.Except;
import org.dockbox.hartshorn.di.InjectionPoint;
import org.dockbox.hartshorn.proxy.annotations.Instance;
import org.dockbox.hartshorn.proxy.annotations.Proxy;
import org.dockbox.hartshorn.proxy.annotations.Proxy.Target;
import org.dockbox.hartshorn.proxy.exception.CancelProxyException;
import org.dockbox.hartshorn.proxy.handle.ProxyHandler;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.dockbox.hartshorn.util.Reflect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public final class ProxyableBootstrap {

    private ProxyableBootstrap() {}

    static void boostrapDelegates() {
        Hartshorn.log().info("Scanning for proxy types in " + Hartshorn.PACKAGE_PREFIX);
        Reflect.types(Proxy.class).forEach(proxy -> {
            Hartshorn.log().info("Processing [" + proxy.getCanonicalName() + "]");
            if (Reflect.isAbstract(proxy)) {
                Hartshorn.log().warn("Proxy source cannot be abstract [" + proxy.getCanonicalName() + "]");
                return;
            }

            Proxy delegationInfo = Reflect.annotation(proxy, Proxy.class).get();
            if (Reflect.annotation(delegationInfo.value(), Proxy.class).present()) {
                Hartshorn.log().warn("Proxy target cannot be another delegate [" + proxy.getCanonicalName() + "]");
                return;
            }

            ProxyableBootstrap.delegateMethods(proxy);
        });
    }

    private static void delegateMethods(Class<?> proxyClass) {
        @NotNull
        @Unmodifiable
        Collection<Method> targets = Reflect.methods(proxyClass, Target.class, i -> true, false);

        Proxy proxy = Reflect.annotation(proxyClass, Proxy.class).get();
        targets.forEach(target -> ProxyableBootstrap.delegateMethod(proxyClass, proxy.value(), target));
    }

    private static <T, C> void delegateMethod(Class<T> proxyClass, Class<C> proxyTargetClass, Method source) {
        Hartshorn.log().info("Processing " + proxyClass.getSimpleName() + "." + source.getName());
        if (Modifier.isAbstract(source.getModifiers())) {
            Hartshorn.log().warn("Proxy method cannot be abstract [" + source.getName() + "]");
            return;
        }

        String methodName = source.getName();

        // Used only for method lookup, parameters decorated with @Instance are injected and thus not
        // present on the
        // target method.
        Class<?>[] arguments = HartshornUtils.asList(source.getParameters()).stream()
                .filter(arg -> Reflect.annotation(arg, Instance.class).absent())
                .map(Parameter::getType)
                .toArray(Class<?>[]::new);

        // By default the name of the proxy method is used, however it's possible two methods proxy
        // separate stages of
        // the same method, so the method names are different. The @Target annotation allows setting the
        // method name
        // manually.
        Target target = Reflect.annotation(source, Target.class).get();
        if (!target.method().isEmpty()) methodName = target.method();
        try {
            Method targetMethod = proxyTargetClass.getDeclaredMethod(methodName, arguments);

            // If the target method has a return type other than `void`, and the proxy wants to overwrite
            // the return
            // value, we need to ensure the return value of the proxy method is assignable from the return
            // type of the
            // target.
            if (!source.getReturnType().equals(Void.TYPE) && target.overwrite()) {
                if (!Reflect.assigns(source.getReturnType(), targetMethod.getReturnType())) {
                    Hartshorn.log().warn("Return type for '" + source.getName() + "' is not assignable from '" + targetMethod
                            .getReturnType() + "' [" + proxyClass.getCanonicalName() + "]");
                    return;
                }
            }

            ProxyAttribute<C, ?> property = ProxyAttribute.of(proxyTargetClass, targetMethod, (instance, args, proxyContext) -> {
                Object[] invokingArgs = ProxyableBootstrap.prepareArguments(source, args, instance);
                try {
                    return source.invoke(Hartshorn.context().get(proxyClass), invokingArgs);
                }
                catch (CancelProxyException e) {
                    proxyContext.holder().cancelled(true);
                }
                catch (Throwable e) {
                    Except.handle(e);
                }
                //noinspection ReturnOfNull
                return null;
            });
            property.phase(target.at());
            property.priority(target.priority());
            property.overwriteResult(target.overwrite());
            InjectionPoint<C> point = InjectionPoint.of(proxyTargetClass, instance -> {
                try {
                    ProxyHandler<C> handler = new ProxyHandler<>(instance);
                    handler.delegate(property);
                    return handler.proxy();
                }
                catch (Throwable t) {
                    Except.handle(t);
                }
                return instance;
            });
            Hartshorn.context().add(point);
        }
        catch (NoSuchMethodException e) {
            Hartshorn.log().warn("Proxy target does not have declared method '" + methodName + "(" + Arrays.toString(arguments) + ") [" + proxyClass
                    .getCanonicalName() + "]");
        }
    }

    private static Object[] prepareArguments(Method method, Object[] args, Object instance) {
        List<Object> arguments = HartshornUtils.emptyList();
        if (method.getParameterCount() >= 1 && Reflect.annotation(method.getParameters()[0], Instance.class).present()) {
            arguments.add(instance);
        }
        arguments.addAll(Arrays.asList(args));
        return arguments.toArray();
    }
}
