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

package org.dockbox.hartshorn.data.serialization;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.inject.Key;
import org.dockbox.hartshorn.proxy.processing.MethodProxyContext;
import org.dockbox.hartshorn.proxy.processing.ServiceAnnotatedMethodInterceptorPostProcessor;
import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.util.reflect.MethodContext;

import java.lang.annotation.Annotation;
import java.util.Optional;

public abstract class AbstractSerializerPostProcessor<A extends Annotation> extends ServiceAnnotatedMethodInterceptorPostProcessor<A> {

    private static final Key<SerializationSourceConverter> CONVERTER_KEY = Key.of(SerializationSourceConverter.class);

    @Override
    public <T> boolean preconditions(final ApplicationContext context, final MethodProxyContext<T> methodContext, final ComponentProcessingContext processingContext) {
        final SerializationSourceConverter converter = findConverter(context, methodContext, processingContext);
        return converter != null;
    }

    protected <T> SerializationSourceConverter findConverter(final ApplicationContext context, final MethodProxyContext<T> methodContext, final ComponentProcessingContext processingContext) {
        if (processingContext.containsKey(CONVERTER_KEY)) {
            return processingContext.get(CONVERTER_KEY);
        }

        final MethodContext<?, T> method = methodContext.method();
        final SerializationSourceConverter converter = method.annotation(SerializationSource.class)
                .map(serializationSource -> (SerializationSourceConverter) context.get(serializationSource.converter()))
                .orElse(() -> context.get(ArgumentSerializationSourceConverter.class))
                .rethrowUnchecked().orNull();

        if (converter != null) {
            processingContext.put(CONVERTER_KEY, converter);
        }
        return converter;
    }

    protected Object wrapSerializationResult(final MethodContext<?, ?> methodContext, final Result<?> result) {
        if (methodContext.returnType().is(Result.class))
            return result;
        else if (methodContext.returnType().is(Optional.class))
            return Optional.ofNullable(result.orNull());
        else return result.orNull();
    }
}
