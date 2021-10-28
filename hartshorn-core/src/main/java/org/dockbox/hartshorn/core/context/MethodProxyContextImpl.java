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

package org.dockbox.hartshorn.core.context;

import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.properties.Attribute;

import java.lang.annotation.Annotation;

import lombok.Getter;

@Getter
public class MethodProxyContextImpl<T> extends DefaultContext implements MethodProxyContext<T> {

    private final T instance;
    private final TypeContext<T> type;
    private final MethodContext<?, T> method;
    private final Attribute<?>[] properties;
    private final ApplicationContext context;

    public MethodProxyContextImpl(final ApplicationContext context, final T instance, final TypeContext<T> type, final MethodContext<?, T> method, final Attribute<?>[] properties) {
        this.context = context;
        this.instance = instance;
        this.type = type;
        this.method = method;
        this.properties = properties;
    }

    @Override
    public <A extends Annotation> A annotation(final Class<A> annotation) {
        return this.method.annotation(annotation).orNull();
    }
}
