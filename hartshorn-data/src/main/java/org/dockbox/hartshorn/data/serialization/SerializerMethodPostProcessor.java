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

import java.io.OutputStream;
import java.util.List;

public class SerializerMethodPostProcessor extends AbstractSerializerPostProcessor<Serialize> {

    @Override
    public Class<Serialize> annotation() {
        return Serialize.class;
    }

    @Override
    public <T, R> MethodInterceptor<T> process(final ApplicationContext context, final MethodProxyContext<T> methodContext, final ComponentProcessingContext processingContext) {
        final SerializationSourceConverter converter = this.findConverter(context, methodContext, processingContext);
        final MethodContext<?, T> method = methodContext.method();
        final Serialize serialize = method.annotation(Serialize.class).get();
        final ObjectMapper mapper = context.get(ObjectMapper.class).fileType(serialize.fileType());
        final boolean returnsStringOrWrapper = this.returnsStringOrWrapper(method);

        return interceptorContext -> {
            final Object[] arguments = interceptorContext.args();

            try (final OutputStream outputStream = converter.outputStream(method, arguments)) {
                final Result<?> result;

                if (outputStream == null && returnsStringOrWrapper) result = mapper.write(arguments[0]);
                else result = mapper.write(outputStream, arguments[0]);

                return this.wrapSerializationResult(method, result);
            }
        };
    }

    private boolean returnsStringOrWrapper(final MethodContext<?, ?> method) {
        if (method.returnType().is(String.class)) return true;
        final List<TypeContext<?>> typeParameters = method.genericReturnType().typeParameters();
        return typeParameters.size() == 1 && typeParameters.get(0).is(String.class);
    }
}
