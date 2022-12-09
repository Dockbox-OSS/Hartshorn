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

package org.dockbox.hartshorn.config;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.config.annotations.SerializationSource;
import org.dockbox.hartshorn.proxy.processing.MethodProxyContext;
import org.dockbox.hartshorn.proxy.processing.ServiceAnnotatedMethodInterceptorPostProcessor;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.option.Option;

import java.lang.annotation.Annotation;
import java.util.Optional;

public abstract class AbstractSerializerPostProcessor<A extends Annotation> extends ServiceAnnotatedMethodInterceptorPostProcessor<A> {

    private static final ComponentKey<SerializationSourceConverter> CONVERTER_KEY = ComponentKey.of(SerializationSourceConverter.class);

    @Override
    public <T> boolean preconditions(final ApplicationContext context, final MethodProxyContext<T> methodContext, final ComponentProcessingContext<T> processingContext) {
        final SerializationSourceConverter converter = this.findConverter(context, methodContext, processingContext);
        return converter != null;
    }

    protected <T> SerializationSourceConverter findConverter(final ApplicationContext context, final MethodProxyContext<T> methodContext, final ComponentProcessingContext<T> processingContext) {
        if (processingContext.containsKey(CONVERTER_KEY)) {
            return processingContext.get(CONVERTER_KEY);
        }

        final MethodView<T, ?> method = methodContext.method();
        final SerializationSourceConverter converter = method.annotations().get(SerializationSource.class)
                .map(serializationSource -> (SerializationSourceConverter) context.get(serializationSource.converter()))
                .orCompute(() -> context.get(ArgumentSerializationSourceConverter.class))
                .orNull();

        if (converter != null) {
            processingContext.put(CONVERTER_KEY, converter);
        }
        return converter;
    }

    protected Object wrapSerializationResult(final MethodView<?, ?> methodContext, final Option<?> result) {
        if (methodContext.returnType().is(Option.class))
            return result;
        else if (methodContext.returnType().is(Optional.class))
            return Optional.ofNullable(result.orNull());
        else return result.orNull();
    }
}