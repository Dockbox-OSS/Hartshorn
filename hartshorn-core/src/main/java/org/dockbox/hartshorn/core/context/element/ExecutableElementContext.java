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

package org.dockbox.hartshorn.core.context.element;

import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.annotations.inject.Context;
import org.dockbox.hartshorn.core.context.ApplicationContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ExecutableElementContext<A extends Executable> extends AnnotatedMemberContext<A> {

    private LinkedList<ParameterContext<?>> parameters;

    public List<ParameterContext<?>> parameters(final Class<? extends Annotation> annotation) {
        return this.parameters().stream()
                .filter(parameter -> parameter.annotation(annotation).present())
                .toList();
    }

    public LinkedList<ParameterContext<?>> parameters() {
        if (this.parameters == null) {
            final LinkedList<ParameterContext<?>> parameters = new LinkedList<>();
            for (final Parameter parameter : this.element().getParameters()) {
                parameters.add(ParameterContext.of(parameter));
            }
            this.parameters = parameters;
        }
        return this.parameters;
    }

    public LinkedList<TypeContext<?>> parameterTypes() {
        return this.parameters().stream().map(ParameterContext::type).collect(Collectors.toCollection(LinkedList::new));
    }

    public int parameterCount() {
        return this.element().getParameterCount();
    }

    protected Object[] arguments(final ApplicationContext context) {
        final Object[] args = new Object[this.parameterCount()];
        for (int i = 0; i < this.parameterCount(); i++) {
            final TypeContext<?> parameter = this.parameterTypes().get(i);
            final Exceptional<Context> annotation = parameter.annotation(org.dockbox.hartshorn.core.annotations.inject.Context.class);
            if (annotation.present() && parameter.childOf(org.dockbox.hartshorn.core.context.Context.class)) {
                final String contextName = annotation.get().value();
                if ("".equals(contextName)) args[i] = context.first((Class<? extends org.dockbox.hartshorn.core.context.Context>) parameter.type());
                else context.first(contextName, (Class<? extends org.dockbox.hartshorn.core.context.Context>) parameter.type());
            }
            else {
                args[i] = context.get(parameter);
            }
        }
        return args;
    }
}
