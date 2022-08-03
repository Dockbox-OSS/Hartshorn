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
import org.dockbox.hartshorn.data.mapping.ObjectMapper;
import org.dockbox.hartshorn.proxy.MethodInterceptor;
import org.dockbox.hartshorn.proxy.processing.MethodProxyContext;
import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.util.reflect.MethodContext;
import org.dockbox.hartshorn.util.reflect.TypeContext;

import java.io.InputStream;

public class DeserializerMethodPostProcessor extends AbstractSerializerPostProcessor<Deserialize> {
    @Override
    public Class<Deserialize> annotation() {
        return Deserialize.class;
    }

    @Override
    public <T, R> MethodInterceptor<T> process(final ApplicationContext context, final MethodProxyContext<T> methodContext, final ComponentProcessingContext processingContext) {
        final SerializationSourceConverter converter = this.findConverter(context, methodContext, processingContext);
        final MethodContext<?, T> method = methodContext.method();
        final Deserialize serialize = method.annotation(Deserialize.class).get();
        final ObjectMapper mapper = context.get(ObjectMapper.class).fileType(serialize.fileType());
        final TypeContext<?> returnType = method.genericReturnType();

        return interceptorContext -> {
            try (final InputStream inputStream = converter.inputStream(method, interceptorContext.args())) {
                final Result<?> result = mapper.read(inputStream, returnType);
                return this.wrapSerializationResult(method, result);
            }
        };
    }
}
