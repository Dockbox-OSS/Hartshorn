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

package org.dockbox.hartshorn.component.factory;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.processing.ComponentPreProcessor;
import org.dockbox.hartshorn.component.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.component.processing.ExitingComponentProcessor;
import org.dockbox.hartshorn.component.processing.ProcessingOrder;
import org.dockbox.hartshorn.inject.ContextDrivenProvider;
import org.dockbox.hartshorn.inject.Key;
import org.dockbox.hartshorn.inject.Provider;
import org.dockbox.hartshorn.inject.processing.BindingProcessor;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.util.List;
import java.util.stream.Collectors;

public class FactoryServicePreProcessor extends ComponentPreProcessor implements ExitingComponentProcessor {

    @Override
    public <T> void process(final ApplicationContext context, final ComponentProcessingContext<T> processingContext) {
        final List<MethodView<T, ?>> factoryMethods = processingContext.type().methods().annotatedWith(Factory.class);
        if (factoryMethods.isEmpty()) return;

        final FactoryContext factoryContext = context.first(FactoryContext.class).get();

        methods:
        for (final MethodView<T, ?> method : factoryMethods) {
            final Factory annotation = method.annotations().get(Factory.class).get();
            Key<?> returnKey = Key.of(method.returnType());
            if (!"".equals(annotation.value())) returnKey = returnKey.name(annotation.value());

            final List<Class<?>> methodParameters = method.parameters().types().stream()
                    .map(TypeView::type)
                    .collect(Collectors.toList());

            for (final Provider<?> provider : context.hierarchy(returnKey).providers()) {
                if (provider instanceof ContextDrivenProvider<?> contextDrivenProvider) {
                    final TypeView<?> typeContext = context.environment().introspect(contextDrivenProvider.type());

                    for (final ConstructorView<?> constructor : typeContext.constructors().bound()) {
                        if (constructor.parameters().matches(methodParameters)) {
                            factoryContext.register((MethodView<Object, ?>) method, (ConstructorView<Object>) constructor);
                            continue methods;
                        }
                    }
                }
            }

            if (annotation.required()) throw new MissingFactoryConstructorException(returnKey, method);
        }
    }

    @Override
    public Integer order() {
        return ProcessingOrder.FIRST;
    }

    @Override
    public void exit(final ApplicationContext context) {
        try {
            context.get(BindingProcessor.class).finalizeProxies(context);
        } catch (final ApplicationException e) {
            context.handle(e);
        }
    }
}
