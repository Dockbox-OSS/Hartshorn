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

import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.di.context.ApplicationContext;
import org.dockbox.selene.di.properties.InjectorProperty;
import org.dockbox.selene.di.services.ServiceModifier;
import org.dockbox.selene.proxy.exception.ProxyMethodBindingException;
import org.dockbox.selene.proxy.handle.ProxyFunction;
import org.dockbox.selene.proxy.handle.ProxyHandler;
import org.dockbox.selene.util.Reflect;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;

public interface ServiceAnnotatedMethodModifier<A extends Annotation> extends ServiceModifier {

    @Override
    default <T> boolean preconditions(Class<T> type, @Nullable T instance, InjectorProperty<?>... properties) {
        return !Reflect.annotatedMethods(type, this.annotation()).isEmpty();
    }

    @Override
    default <T> T process(ApplicationContext context, Class<T> type, @Nullable T instance, InjectorProperty<?>... properties) {
        final Collection<Method> methods = Reflect.annotatedMethods(type, this.annotation());
        ProxyHandler<T> handler = new ProxyHandler<>(instance, type);
        for (Method method : methods) {
            if (this.preconditions(type, instance, method, properties)) {
                final ProxyFunction<T, Object> function = this.process(context, type, instance, method, properties);
                if (function != null) {
                    ProxyProperty<T, ?> property = ProxyProperty.of(type, method, function);
                    handler.delegate(property);
                }
            } else {
                if (this.failOnPrecondition()) {
                    throw new ProxyMethodBindingException(method);
                }
            }
        }
        return Exceptional.of(handler::proxy).or(instance);
    }

    <T, R> ProxyFunction<T, R> process(ApplicationContext context, Class<T> type, @Nullable T instance, Method method, InjectorProperty<?>... properties);

    <T> boolean preconditions(Class<T> type, @Nullable T instance, Method method, InjectorProperty<?>... properties);

    boolean failOnPrecondition();

    Class<A> annotation();
}
