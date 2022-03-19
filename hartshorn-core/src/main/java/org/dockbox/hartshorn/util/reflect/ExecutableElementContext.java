/*
 * Copyright 2019-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dockbox.hartshorn.util.reflect;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.application.context.ParameterLoaderContext;
import org.dockbox.hartshorn.util.Named;
import org.dockbox.hartshorn.util.reflect.parameter.ExecutableElementContextParameterLoader;

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
