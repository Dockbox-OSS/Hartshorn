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
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.processing.Binds;
import org.dockbox.hartshorn.component.processing.ComponentPreProcessor;
import org.dockbox.hartshorn.component.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.component.processing.ExitingComponentProcessor;
import org.dockbox.hartshorn.component.processing.ProcessingPriority;
import org.dockbox.hartshorn.inject.Context;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.introspect.view.AnnotatedElementView;
import org.dockbox.hartshorn.util.introspect.view.FieldView;
import org.dockbox.hartshorn.util.introspect.view.GenericTypeView;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.inject.Named;

public final class ProviderServicePreProcessor extends ComponentPreProcessor implements ExitingComponentProcessor {

    @Override
    public <T> void process(final ApplicationContext context, final ComponentProcessingContext<T> processingContext) {
        final List<MethodView<T, ?>> methods = processingContext.type().methods().annotatedWith(Binds.class);
        final List<FieldView<T, ?>> fields = processingContext.type().fields().annotatedWith(Binds.class);

        if (methods.isEmpty() && fields.isEmpty()) return;

        context.log().debug("Found " + (methods.size() + fields.size()) + " method providers in " + processingContext.type().name());

        final ProviderContextList providerContext = context.first(ProviderContextList.class).orNull();
        for (final MethodView<T, ?> method : methods) {
            // TODO: ProviderDependencyResolver
            // - Resolve dependencies for method
            // - Resolve dependencies for field
            // - Resolve dependencies for constructor
            // - more?
            final Set<? extends ComponentKey<?>> dependencies = method.parameters().all().stream()
                    .map(parameter -> {
                        if (parameter.type().annotations().has(Context.class)) {
                            return null;
                        }
                        final TypeView<?> type = parameter.type();
                        final ComponentKey.Builder<?> builder = ComponentKey.builder(type.type());
                        if (parameter.annotations().has(Named.class)) {
                            builder.name(parameter.annotations().get(Named.class).get());
                        }
                        return builder.build();
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            this.register(providerContext, method, Set.copyOf(dependencies));
        }
        for (final FieldView<T, ?> field : fields) {
            this.register(providerContext, field, Set.of());
        }
    }

    private <E extends AnnotatedElementView & GenericTypeView<?>> void register(final ProviderContextList context, final E element, final Set<ComponentKey<?>> dependencies) {
        final ComponentKey<?> key = this.key(element);
        final Binds binding = element.annotations().get(Binds.class).get();
        final ProviderContext providerContext = new ProviderContext(key, element, binding);
        providerContext.dependencies(dependencies);
        context.add(providerContext);
    }

    @Override
    public void exit(final ApplicationContext context) {
        final BindingProcessor processor = new BindingProcessor();
        context.bind(BindingProcessor.class).singleton(processor);

        final ProviderContextList providerContext = context.first(ProviderContextList.class).orNull();
        try {
            processor.process(providerContext, context);
        } catch (final ApplicationException e) {
            context.handle(e);
        }
    }

    private <E extends AnnotatedElementView & GenericTypeView<?>> ComponentKey<?> key(final E element) {
        final Binds annotation = element.annotations().get(Binds.class).get();
        if (element.type().is(Class.class) || element.type().is(TypeView.class)) {
            final TypeView<?> view = element.genericType().typeParameters().at(0).get();
            return ComponentKey.of(view.type(), annotation.value());
        }
        else {
            return ComponentKey.of(element.type().type(), annotation.value());
        }
    }

    @Override
    public int priority() {
        // +512 to allow third parties to register their own providers
        return ProcessingPriority.HIGHEST_PRECEDENCE + 512;
    }
}
