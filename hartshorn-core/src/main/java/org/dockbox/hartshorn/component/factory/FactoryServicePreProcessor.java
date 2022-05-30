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
import org.dockbox.hartshorn.component.processing.ProcessingOrder;
import org.dockbox.hartshorn.component.processing.ServicePreProcessor;
import org.dockbox.hartshorn.inject.ContextDrivenProvider;
import org.dockbox.hartshorn.inject.Key;
import org.dockbox.hartshorn.inject.Provider;
import org.dockbox.hartshorn.util.reflect.ConstructorContext;
import org.dockbox.hartshorn.util.reflect.MethodContext;
import org.dockbox.hartshorn.util.reflect.TypeContext;

import java.util.LinkedList;

public class FactoryServicePreProcessor implements ServicePreProcessor {

    @Override
    public boolean preconditions(final ApplicationContext context, final Key<?> key) {
        return !key.type().methods(Factory.class).isEmpty();
    }

    @Override
    public <T> void process(final ApplicationContext context, final Key<T> key) {
        final FactoryContext factoryContext = context.first(FactoryContext.class).get();

        methods:
        for (final MethodContext<?, T> method : key.type().methods(Factory.class)) {
            final Factory annotation = method.annotation(Factory.class).get();
            Key<?> returnKey = Key.of(method.returnType());
            if (!"".equals(annotation.value())) returnKey = returnKey.name(annotation.value());

            for (final Provider<?> provider : context.hierarchy(returnKey).providers()) {
                if (provider instanceof ContextDrivenProvider contextDrivenProvider) {
                    final TypeContext<?> typeContext = ((ContextDrivenProvider<?>) provider).context();

                    for (final ConstructorContext<?> constructor : typeContext.boundConstructors()) {
                        final LinkedList<TypeContext<?>> constructorParameters = constructor.parameterTypes();
                        final LinkedList<TypeContext<?>> methodParameters = method.parameterTypes();

                        if (methodParameters.equals(constructorParameters)) {
                            factoryContext.register((MethodContext<Object, ?>) method, (ConstructorContext<Object>) constructor);
                            continue methods;
                        }
                    }
                }
            }

            if (annotation.required())
                throw new IllegalStateException("No matching bound constructor found for " + returnKey + " with parameters: " + method.parameterTypes().stream().map(TypeContext::type).toList());
        }
    }

    @Override
    public Integer order() {
        return ProcessingOrder.FIRST;
    }
}
