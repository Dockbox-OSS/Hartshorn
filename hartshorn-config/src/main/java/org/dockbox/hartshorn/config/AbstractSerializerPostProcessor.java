/*
 * Copyright 2019-2024 the original author or authors.
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

import java.lang.annotation.Annotation;
import java.util.Optional;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.component.processing.proxy.MethodProxyContext;
import org.dockbox.hartshorn.component.processing.proxy.ServiceAnnotatedMethodInterceptorPostProcessor;
import org.dockbox.hartshorn.config.annotations.SerializationSource;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.option.Option;

/**
 * TODO: #1062 Add documentation
 *
 * @param <A> ...
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public abstract class AbstractSerializerPostProcessor<A extends Annotation> extends ServiceAnnotatedMethodInterceptorPostProcessor<A> {

    @Override
    public <T> boolean preconditions(ApplicationContext context, MethodProxyContext<T> methodContext, ComponentProcessingContext<T> processingContext) {
        SerializationSourceConverter converter = this.findConverter(context, methodContext, processingContext);
        return converter != null;
    }

    protected <T> SerializationSourceConverter findConverter(ApplicationContext context, MethodProxyContext<T> methodContext, ComponentProcessingContext<T> processingContext) {
        if (processingContext.containsKey(SerializationSourceConverter.class)) {
            return processingContext.get(SerializationSourceConverter.class);
        }

        MethodView<T, ?> method = methodContext.method();
        SerializationSourceConverter converter = method.annotations().get(SerializationSource.class)
                .map(serializationSource -> (SerializationSourceConverter) context.get(serializationSource.converter()))
                .orCompute(ArgumentSerializationSourceConverter::new)
                .orNull();

        if (converter != null) {
            processingContext.put(SerializationSourceConverter.class, converter);
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
