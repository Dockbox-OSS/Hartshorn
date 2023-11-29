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

package org.dockbox.hartshorn.component.factory;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.processing.ComponentPreProcessor;
import org.dockbox.hartshorn.component.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.component.processing.ProcessingPriority;
import org.dockbox.hartshorn.inject.ContextDrivenProvider;
import org.dockbox.hartshorn.inject.Provider;
import org.dockbox.hartshorn.inject.binding.Bound;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @deprecated See {@link Factory}.
 */
@Deprecated(since = "0.5.0", forRemoval = true)
public class FactoryServicePreProcessor extends ComponentPreProcessor {

    private static final Logger FACTORY_LOGGER = LoggerFactory.getLogger(UseFactoryServices.class);

    public FactoryServicePreProcessor() {
        FACTORY_LOGGER.warn("The @Factory annotation is deprecated and will be removed in a future release. Please use dedicated factory objects or regular object binding instead.");
    }

    @Override
    public <T> void process(ApplicationContext context, ComponentProcessingContext<T> processingContext) {
        List<MethodView<T, ?>> factoryMethods = processingContext.type().methods().annotatedWith(Factory.class);
        if (factoryMethods.isEmpty()) {
            return;
        }

        FactoryContext factoryContext = context.first(FactoryContext.class).get();

        for (MethodView<T, ?> method : factoryMethods) {
            Factory annotation = method.annotations().get(Factory.class).get();
            ComponentKey<?> componentKey = ComponentKey.of(method.returnType());
            if (!"".equals(annotation.value())) {
                componentKey = componentKey.mutable().name(annotation.value()).build();
            }

            if (!lookupMatchingConstructor(context, factoryContext, (MethodView<Object, ?>) method, componentKey)) {
                if (annotation.required()) {
                    throw new MissingFactoryConstructorException(componentKey, method);
                }
            }
        }
    }

    private static boolean lookupMatchingConstructor(ApplicationContext context, FactoryContext factoryContext,
                                                     MethodView<Object, ?> method, ComponentKey<?> componentKey) {
        List<Class<?>> methodParameters = method.parameters().types().stream()
                .map(TypeView::type)
                .collect(Collectors.toList());

        for (Provider<?> provider : context.hierarchy(componentKey).providers()) {
            if (provider instanceof ContextDrivenProvider<?> contextDrivenProvider) {
                TypeView<?> typeContext = context.environment().introspector().introspect(contextDrivenProvider.type());

                for (ConstructorView<?> constructor : typeContext.constructors().annotatedWith(Bound.class)) {
                    if (constructor.parameters().matches(methodParameters)) {
                        factoryContext.register(method, (ConstructorView<Object>) constructor);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public int priority() {
        // +1024 to be after the default binding processor (+512), but allow for other processors to be in between
        return ProcessingPriority.HIGHEST_PRECEDENCE + 1024;
    }
}
