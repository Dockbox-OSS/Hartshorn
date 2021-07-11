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

package org.dockbox.hartshorn.proxy.service;

import org.dockbox.hartshorn.di.context.DefaultContext;
import org.dockbox.hartshorn.di.properties.InjectorProperty;
import org.dockbox.hartshorn.util.Reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import lombok.Getter;

@Getter
public class SimpleMethodProxyContext<T> extends DefaultContext implements MethodProxyContext<T> {

    private final T instance;
    private final Class<T> type;
    private final Method method;
    private final InjectorProperty<?>[] properties;
    private final Annotation[] annotations;
    private final Class<?> returnType;

    public SimpleMethodProxyContext(T instance, Class<T> type, Method method, InjectorProperty<?>[] properties) {
        this.instance = instance;
        this.type = type;
        this.method = method;
        this.properties = properties;
        this.annotations = method.getAnnotations();
        this.returnType = method.getReturnType();
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotation) {
        return Reflect.annotation(this.method, annotation).orNull();
    }
}
