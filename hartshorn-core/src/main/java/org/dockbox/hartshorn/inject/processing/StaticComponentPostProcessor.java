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

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.ParameterizableType;
import org.dockbox.hartshorn.component.contextual.StaticComponentContext;
import org.dockbox.hartshorn.component.processing.ComponentPostProcessor;
import org.dockbox.hartshorn.component.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.util.introspect.TypeParameterList;
import org.dockbox.hartshorn.util.introspect.convert.ConversionService;
import org.dockbox.hartshorn.util.introspect.view.TypeParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.util.Collection;
import java.util.List;

public class StaticComponentPostProcessor extends ComponentPostProcessor {

    @Override
    public <T> T initializeComponent(final ApplicationContext context, @Nullable final T instance,
                                     final ComponentProcessingContext<T> processingContext) {
        final ComponentKey<T> componentKey = processingContext.key();

        if (Collection.class.isAssignableFrom(componentKey.type())) {
            final TypeView<T> componentType = context.environment().introspector().introspect(componentKey.type());
            final List<ParameterizableType<?>> parameters = componentKey.parameterizedType().parameters();
            final ParameterizableType<?> elementType;
            if(parameters.size() == 1) {
                elementType = parameters.get(0);
            }
            else {
                final TypeParameterList collectionParameters = componentType.typeParameters()
                        .resolveInputFor(Collection.class);
                final TypeParameterView parameterView = collectionParameters.atIndex(0)
                        .orElseThrow(() -> new IllegalArgumentException(String.format("Cannot determine collection type for %s", componentKey.type())));
                elementType = new ParameterizableType<>(parameterView.resolvedType().get());
            }

            final ComponentKey<?> elementKey = ComponentKey.builder(elementType)
                    .name(componentKey.name())
                    .build();
            final StaticComponentContext staticComponentContext = context.first(StaticComponentContext.CONTEXT_KEY).get();
            final List<?> beans = staticComponentContext.provider().all(elementKey);

            return context.get(ConversionService.class).convert(beans, componentType.type());
        }
        return instance;
    }
}
