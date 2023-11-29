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

package org.dockbox.hartshorn.config;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.config.annotations.SerializationSource;
import org.dockbox.hartshorn.component.processing.proxy.MethodProxyContext;
import org.dockbox.hartshorn.component.processing.proxy.ServiceAnnotatedMethodInterceptorPostProcessor;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.option.Option;

import java.lang.annotation.Annotation;
import java.util.Optional;

public abstract class AbstractSerializerPostProcessor<A extends Annotation> extends ServiceAnnotatedMethodInterceptorPostProcessor<A> {

    private static final ComponentKey<SerializationSourceConverter> CONVERTER_KEY = ComponentKey.of(SerializationSourceConverter.class);

    @Override
    public <T> boolean preconditions(ApplicationContext context, MethodProxyContext<T> methodContext, ComponentProcessingContext<T> processingContext) {
        SerializationSourceConverter converter = this.findConverter(context, methodContext, processingContext);
        return converter != null;
    }

    protected <T> SerializationSourceConverter findConverter(ApplicationContext context, MethodProxyContext<T> methodContext, ComponentProcessingContext<T> processingContext) {
        if (processingContext.containsKey(CONVERTER_KEY)) {
            return processingContext.get(CONVERTER_KEY);
        }

        MethodView<T, ?> method = methodContext.method();
        SerializationSourceConverter converter = method.annotations().get(SerializationSource.class)
                .map(serializationSource -> (SerializationSourceConverter) context.get(serializationSource.converter()))
                .orCompute(ArgumentSerializationSourceConverter::new)
                .orNull();

        if (converter != null) {
            processingContext.put(CONVERTER_KEY, converter);
        }
        return converter;
    }

    protected Object wrapSerializationResult(MethodView<?, ?> methodContext, Option<?> result) {
        if (methodContext.returnType().is(Option.class)) {
            return result;
        }
        else if (methodContext.returnType().is(Optional.class)) {
            return Optional.ofNullable(result.orNull());
        }
        else {
            return result.orNull();
        }
    }
}
