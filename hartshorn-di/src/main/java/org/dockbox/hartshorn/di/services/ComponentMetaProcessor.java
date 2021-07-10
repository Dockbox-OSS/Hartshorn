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

import org.dockbox.hartshorn.di.ApplicationContextAware;
import org.dockbox.hartshorn.di.annotations.component.ComponentLike;
import org.dockbox.hartshorn.di.annotations.component.ComponentLink;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.dockbox.hartshorn.util.Reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.BiFunction;

final class ComponentMetaProcessor<A extends Annotation> {

    private final Map<ComponentAspect, BiFunction<Class<?>, A, ?>> functions = HartshornUtils.emptyMap();

    public ComponentMetaProcessor(Class<A> annotation) {
        if (!annotation.isAnnotationPresent(ComponentLike.class)) throw new IllegalArgumentException("Cannot process non-component decorator");
        for (ComponentAspect aspect : ComponentAspect.values()) {
            BiFunction<Class<?>, A, ?> function = aspect::value;
            this.functions.put(aspect, function);
        }

        for (Method method : Reflect.methods(annotation, ComponentLink.class)) {
            final ComponentLink componentLink = method.getAnnotation(ComponentLink.class);
            final ComponentAspect aspect = componentLink.value();

            if (Reflect.assigns(method.getReturnType(), aspect.type())) {
                BiFunction<Class<?>, A, ?> function = (type, a) -> ApplicationContextAware.instance().getContext().invoke(method, a);
                this.functions.put(aspect, function);
            }
        }
    }

    public <T> T get(ComponentAspect aspect, Class<?> type, A annotation) {
        //noinspection unchecked
        return (T) this.functions.get(aspect).apply(type, annotation);
    }
}
