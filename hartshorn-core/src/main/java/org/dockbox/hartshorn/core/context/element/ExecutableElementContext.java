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

import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.ParameterLoaderContext;
import org.dockbox.hartshorn.core.domain.Named;
import org.dockbox.hartshorn.core.services.parameter.ExecutableElementContextParameterLoader;

import java.lang.annotation.Annotation;
import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The {@link ExecutableElementContext} represents an executable element. Executable elements are methods and constructors.
 * The primary function of an executable element is to accept parameters, and optionally yield a result. The executable
 * cannot be invoked directly through the {@link ExecutableElementContext}, but through the appropriate implementations.
 *
 * @param <A> The type of the executable element.
 * @param <P> The type of the parent of the executable element.
 * @see java.lang.reflect.Constructor
 * @see ConstructorContext
 * @see java.lang.reflect.Method
 * @see MethodContext
 * @author Guus Lieben
 * @since 21.5
 */
public abstract class ExecutableElementContext<A extends Executable, P> extends AnnotatedMemberContext<A> implements Named {

    private TypeContext<P> parent;
    private LinkedList<ParameterContext<?>> parameters;
    protected ExecutableElementContextParameterLoader parameterLoader = new ExecutableElementContextParameterLoader();

    // TODO #584: Continue documentation here
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

    public TypeContext<P> parent() {
        if (this.parent == null) {
            this.parent = (TypeContext<P>) TypeContext.of(this.element().getDeclaringClass());
        }
        return this.parent;
    }

    protected Object[] arguments(final ApplicationContext context) {
        final ParameterLoaderContext loaderContext = new ParameterLoaderContext(this, null, null, context);
        return this.parameterLoader.loadArguments(loaderContext).toArray();
    }
}
