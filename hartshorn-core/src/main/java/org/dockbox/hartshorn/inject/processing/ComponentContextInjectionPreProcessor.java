/*
 * Copyright 2019-2023 the original author or authors.
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

package org.dockbox.hartshorn.inject.processing;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.InvalidComponentException;
import org.dockbox.hartshorn.component.processing.ComponentPreProcessor;
import org.dockbox.hartshorn.component.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.component.processing.ProcessingPriority;
import org.dockbox.hartshorn.inject.Context;
import org.dockbox.hartshorn.util.CollectionUtilities;
import org.dockbox.hartshorn.util.introspect.view.ExecutableElementView;
import org.dockbox.hartshorn.util.introspect.view.FieldView;
import org.dockbox.hartshorn.util.introspect.view.GenericTypeView;
import org.dockbox.hartshorn.util.introspect.view.ParameterView;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ComponentContextInjectionPreProcessor extends ComponentPreProcessor {

    @Override
    public <T> void process(ApplicationContext context, ComponentProcessingContext<T> processingContext) {
        for (FieldView<T, ?> field : processingContext.type().fields().annotatedWith(Context.class)) {
            this.validate(field, processingContext);
        }

        List<ExecutableElementView<T>> constructors = processingContext.type().constructors().injectable()
                .stream().map(constructor -> (ExecutableElementView<T>) constructor)
                .collect(Collectors.toList());

        List<ExecutableElementView<T>> methods = processingContext.type().methods().all().stream()
                .map(method -> (ExecutableElementView<T>) method)
                .collect(Collectors.toList());

        Collection<ExecutableElementView<T>> executables = CollectionUtilities.merge(constructors, methods);

        for (ExecutableElementView<T> executable : executables) {
            for (ParameterView<?> parameter : executable.parameters().annotedWith(Context.class)) {
                this.validate(parameter, processingContext);
            }
        }
    }

    private void validate(GenericTypeView<?> context, ComponentProcessingContext<?> parent) {
        if (!context.type().isChildOf(org.dockbox.hartshorn.context.Context.class)) {
            throw new InvalidComponentException("%s is annotated with %s but does not extend %s".formatted(
                    context.qualifiedName(),
                    Context.class.getSimpleName(),
                    org.dockbox.hartshorn.context.Context.class.getSimpleName())
            );
        }
    }

    @Override
    public int priority() {
        return ProcessingPriority.HIGHEST_PRECEDENCE;
    }
}
