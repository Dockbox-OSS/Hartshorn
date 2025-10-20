/*
 * Copyright 2019-2025 the original author or authors.
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

import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.ComponentRequestContext;
import org.dockbox.hartshorn.inject.InjectionCapableApplication;
import org.dockbox.hartshorn.inject.annotations.Provided;
import org.dockbox.hartshorn.inject.processing.proxy.AnnotatedMethodInterceptorPostProcessor;
import org.dockbox.hartshorn.inject.processing.proxy.MethodProxyContext;
import org.dockbox.hartshorn.inject.targets.InjectionPoint;
import org.dockbox.hartshorn.proxy.advice.intercept.MethodInterceptor;
import org.dockbox.hartshorn.util.introspect.convert.ConversionService;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

/**
 * A post-processor for methods annotated with {@link Provided}.
 *
 * @see Provided
 *
 * @since 0.4.1
 *
 * @author Guus Lieben
 */
public class AnnotatedProviderMethodInterceptorPostProcessor extends AnnotatedMethodInterceptorPostProcessor<Provided> {

    @Override
    public <T, R> MethodInterceptor<T, R> process(InjectionCapableApplication application, MethodProxyContext<T> methodContext, ComponentProcessingContext<T> processingContext) {
        ConversionService conversionService = application.defaultProvider().get(ConversionService.class);
        MethodView<T, ?> method = methodContext.method();

        //noinspection unchecked
        TypeView<R> type = (TypeView<R>) method.returnType();
        ComponentKey<?> componentKey = application.environment()
                .componentKeyResolver()
                .resolve(method, processingContext.key().scope().orElse(application.defaultProvider().scope()));

        InjectionPoint injectionPoint = new InjectionPoint(method);
        ComponentRequestContext requestContext = ComponentRequestContext.createForInjectionPoint(injectionPoint);
        return interceptorContext -> {
            Object result = application.defaultProvider().get(componentKey, requestContext);
            return conversionService.convert(result, type.type());
        };
    }

    @Override
    public <T> boolean preconditions(InjectionCapableApplication application, MethodProxyContext<T> methodContext, ComponentProcessingContext<T> processingContext) {
        return !methodContext.method().returnType().isVoid();
    }

    @Override
    public Class<Provided> annotation() {
        return Provided.class;
    }
}
