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

package org.dockbox.hartshorn.di.services;

import org.dockbox.hartshorn.di.ComponentType;
import org.dockbox.hartshorn.di.annotations.component.ComponentLike;
import org.dockbox.hartshorn.di.binding.Bindings;

import java.lang.annotation.Annotation;
import java.util.function.BiFunction;
import java.util.function.Function;

public enum ComponentAspect {
    ID(String.class, type -> Bindings.serviceId(type, true)),
    NAME(String.class, type -> Bindings.serviceName(type, true)),
    ENABLED(boolean.class, type -> true),
    OWNER(Class.class, type -> type),
    SINGLETON(boolean.class, (type, annotation) -> {
        final ComponentLike componentLike = annotation.annotationType().getAnnotation(ComponentLike.class);
        return componentLike.singleton().booleanValue();
    }),
    TYPE(ComponentType.class, (type, annotation) -> {
        final ComponentLike componentLike = annotation.annotationType().getAnnotation(ComponentLike.class);
        return componentLike.type();
    })
    ;

    private final Class<?> type;
    private final BiFunction<Class<?>, Annotation, ?> defaultFunction;

    private <T> ComponentAspect(Class<T> type, Function<Class<?>, T> defaultFunction) {
        this.type = type;
        this.defaultFunction = (t, a) -> defaultFunction.apply(t);
    }

    private <T> ComponentAspect(Class<T> type, BiFunction<Class<?>, Annotation, T> defaultFunction) {
        this.type = type;
        this.defaultFunction = defaultFunction;
    }

    public Class<?> type() {
        return this.type;
    }

    public Object value(Class<?> value, Annotation annotation) {
        return this.defaultFunction.apply(value, annotation);
    }
}
