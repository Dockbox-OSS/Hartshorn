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

package org.dockbox.selene.proxy;

import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.server.SeleneInformation;
import org.dockbox.selene.core.server.inject.InjectionPoint;
import org.dockbox.selene.core.util.Reflect;
import org.dockbox.selene.core.util.SeleneUtils;
import org.dockbox.selene.proxy.annotations.Instance;
import org.dockbox.selene.proxy.annotations.Proxy;
import org.dockbox.selene.proxy.annotations.Proxy.Target;
import org.dockbox.selene.proxy.exception.CancelProxyException;
import org.dockbox.selene.proxy.handle.ProxyHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public final class ProxyableBootstrap
{

    private ProxyableBootstrap() {}

    static void boostrapDelegates()
    {
        Selene.log().info("Scanning for proxy types in " + SeleneInformation.PACKAGE_PREFIX);
        Reflect.getAnnotatedTypes(SeleneInformation.PACKAGE_PREFIX, Proxy.class).forEach(proxy -> {
            Selene.log().info("Processing [" + proxy.getCanonicalName() + "]");
            if (Modifier.isAbstract(proxy.getModifiers()))
            {
                Selene.log().warn("Proxy source cannot be abstract [" + proxy.getCanonicalName() + "]");
                return;
            }

            Proxy delegationInfo = proxy.getAnnotation(Proxy.class);
            if (delegationInfo.value().isAnnotationPresent(Proxy.class))
            {
                Selene.log().warn("Proxy target cannot be another delegate [" + proxy.getCanonicalName() + "]");
                return;
            }

            ProxyableBootstrap.delegateMethods(proxy);
        });
    }

    private static void delegateMethods(Class<?> proxyClass)
    {
        @NotNull @Unmodifiable Collection<Method> targets = Reflect.getAnnotedMethods(
                proxyClass,
                Target.class,
                i -> true,
                false
        );

        Proxy proxy = proxyClass.getAnnotation(Proxy.class);
        targets.forEach(target -> ProxyableBootstrap.delegateMethod(proxyClass, proxy.value(), target));
    }

    private static <T, C> void delegateMethod(Class<T> proxyClass, Class<C> proxyTargetClass, Method source)
    {
        Selene.log().info("Processing " + proxyClass.getSimpleName() + "." + source.getName());
        if (Modifier.isAbstract(source.getModifiers()))
        {
            Selene.log().warn("Proxy method cannot be abstract [" + source.getName() + "]");
            return;
        }

        String methodName = source.getName();

        // Used only for method lookup, parameters annotated with @Instance are injected and thus not present on the
        // target method.
        Class<?>[] arguments = SeleneUtils.asList(source.getParameters()).stream()
                .filter(arg -> !arg.isAnnotationPresent(Instance.class))
                .map(Parameter::getType)
                .toArray(Class<?>[]::new);

        // By default the name of the proxy method is used, however it's possible two methods proxy separate stages of
        // the same method, so the method names are different. The @Target annotation allows setting the method name
        // manually.
        Target target = source.getAnnotation(Target.class);
        if (!target.method().isEmpty()) methodName = target.method();
        try
        {
            Method targetMethod = proxyTargetClass.getDeclaredMethod(methodName, arguments);

            // If the target method has a return type other than `void`, and the proxy wants to overwrite the return
            // value, we need to ensure the return value of the proxy method is assignable from the return type of the
            // target.
            if (!source.getReturnType().equals(Void.TYPE) && target.overwrite())
            {
                if (!Reflect.isAssignableFrom(source.getReturnType(), targetMethod.getReturnType()))
                {
                    Selene.log().warn("Return type for '" + source.getName() + "' is not assignable from '" + targetMethod
                            .getReturnType() + "' [" + proxyClass.getCanonicalName() + "]");
                    return;
                }
            }

            ProxyProperty<C, ?> property = ProxyProperty.of(proxyTargetClass, targetMethod, (instance, args, holder) -> {
                Object[] invokingArgs = ProxyableBootstrap.prepareArguments(source, args, instance);
                try
                {
                    return source.invoke(Selene.provide(proxyClass), invokingArgs);
                }
                catch (CancelProxyException e)
                {
                    holder.setCancelled(true);
                }
                catch (Throwable e)
                {
                    Selene.handle(e);
                }
                //noinspection ReturnOfNull
                return null;
            });
            property.setTarget(target.at());
            property.setPriority(target.priority());
            property.setOverwriteResult(target.overwrite());
            InjectionPoint<C> point = InjectionPoint.of(proxyTargetClass, instance -> {
                try
                {
                    ProxyHandler<C> handler = new ProxyHandler<>(instance);
                    handler.delegate(property);
                    return handler.proxy();
                }
                catch (Throwable t)
                {
                    Selene.handle(t);
                }
                return instance;
            });
            Selene.getServer().injectAt(point);
        }
        catch (NoSuchMethodException e)
        {
            Selene.log().warn("Proxy target does not have declared method '" + methodName + "(" + Arrays.toString(arguments) + ") [" + proxyClass
                    .getCanonicalName() + "]");
        }
    }

    private static Object[] prepareArguments(Method method, Object[] args, Object instance)
    {
        List<Object> arguments = SeleneUtils.emptyList();
        if (method.getParameters()[0].isAnnotationPresent(Instance.class))
        {
            arguments.add(instance);
        }
        arguments.addAll(Arrays.asList(args));
        return arguments.toArray();
    }

}
