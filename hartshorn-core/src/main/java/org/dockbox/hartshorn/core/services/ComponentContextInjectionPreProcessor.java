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

package org.dockbox.hartshorn.core.services;

import org.dockbox.hartshorn.core.CollectionUtilities;
import org.dockbox.hartshorn.core.Key;
import org.dockbox.hartshorn.core.annotations.activate.AutomaticActivation;
import org.dockbox.hartshorn.core.annotations.inject.Context;
import org.dockbox.hartshorn.core.annotations.stereotype.Service;
import org.dockbox.hartshorn.core.boot.ExceptionHandler;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.ExecutableElementContext;
import org.dockbox.hartshorn.core.context.element.FieldContext;
import org.dockbox.hartshorn.core.context.element.ParameterContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.context.element.TypedElementContext;
import org.dockbox.hartshorn.core.exceptions.ApplicationException;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@AutomaticActivation
public class ComponentContextInjectionPreProcessor extends ComponentPreValidator<Service> {

    @Override
    public Class<Service> activator() {
        return Service.class;
    }

    @Override
    public <T> void process(final ApplicationContext context, final Key<T> key) {
        final TypeContext<T> type = key.type();
        for (final FieldContext<?> field : type.fields(Context.class))
            this.validate(field, key);

        final List<ExecutableElementContext<?, ?>> constructors = type.injectConstructors().stream()
                .map(c -> (ExecutableElementContext<?, ?>) c)
                .collect(Collectors.toList());

        final List<ExecutableElementContext<?, ?>> methods = type.methods().stream()
                .map(m -> (ExecutableElementContext<?, ?>) m)
                .collect(Collectors.toList());

        final Collection<ExecutableElementContext<?, ?>> executables = CollectionUtilities.merge(constructors, methods);

        for (final ExecutableElementContext<?, ?> executable : executables)
            for (final ParameterContext<?> parameter : executable.parameters(Context.class))
                this.validate(parameter, key);
    }

    private void validate(final TypedElementContext<?> context, final Key<?> parent) {
        if (!context.type().childOf(org.dockbox.hartshorn.core.context.Context.class))
            ExceptionHandler.unchecked(new ApplicationException("%s is annotated with %s but does not extend %s".formatted(
                    context.qualifiedName(),
                    Context.class.getSimpleName(),
                    org.dockbox.hartshorn.core.context.Context.class.getSimpleName())
            ));
    }
}
