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

package org.dockbox.hartshorn.component.processing.proxy;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.proxy.MethodInterceptor;
import org.dockbox.hartshorn.inject.Provided;
import org.dockbox.hartshorn.util.introspect.convert.ConversionService;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

public class ContextMethodPostProcessor extends ServiceAnnotatedMethodInterceptorPostProcessor<Provided> {

    @Override
    public <T, R> MethodInterceptor<T, R> process(final ApplicationContext context, final MethodProxyContext<T> methodContext, final ComponentProcessingContext<T> processingContext) {
        final ConversionService conversionService = context.get(ConversionService.class);
        final Provided annotation = methodContext.annotation(Provided.class);
        final String name = annotation.value();

        //noinspection unchecked
        final TypeView<R> type = (TypeView<R>) methodContext.method().returnType();
        ComponentKey<?> key = ComponentKey.of(type);
        if (!name.isEmpty()) key = key.mutable().name(name).build();

        final ComponentKey<?> finalKey = key;
        return interceptorContext -> conversionService.convert(context.get(finalKey), type.type());
    }

    @Override
    public <T> boolean preconditions(final ApplicationContext context, final MethodProxyContext<T> methodContext, final ComponentProcessingContext<T> processingContext) {
        return !methodContext.method().returnType().isVoid();
    }

    @Override
    public Class<Provided> annotation() {
        return Provided.class;
    }
}
