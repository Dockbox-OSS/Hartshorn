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

import java.io.OutputStream;

import org.dockbox.hartshorn.proxy.advice.intercept.MethodInterceptor;
import org.dockbox.hartshorn.proxy.advice.intercept.MethodInterceptorContext;
import org.dockbox.hartshorn.util.introspect.convert.ConversionService;
import org.dockbox.hartshorn.util.introspect.view.MethodView;

/**
 * TODO: #1062 Add documentation
 *
 * @param <T> ...
 * @param <R>> ...
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class SerializerMethodInterceptor<T, R> implements MethodInterceptor<T, R> {
    private final SerializationSourceConverter converter;
    private final MethodView<T, R> method;
    private final boolean returnsStringOrWrapper;
    private final ObjectMapper mapper;
    private final ConversionService conversionService;

    public SerializerMethodInterceptor(SerializationSourceConverter converter, MethodView<T, R> method,
                                       boolean returnsStringOrWrapper, ObjectMapper mapper,
                                       ConversionService conversionService) {
        this.converter = converter;
        this.method = method;
        this.returnsStringOrWrapper = returnsStringOrWrapper;
        this.mapper = mapper;
        this.conversionService = conversionService;
    }

    @Override
    public R intercept(MethodInterceptorContext<T, R> interceptorContext) throws Throwable {
        Object[] arguments = interceptorContext.args();

        try (OutputStream outputStream = this.converter.outputStream(this.method, arguments)) {
            Object result;

            if (outputStream == null && this.returnsStringOrWrapper) {
                result = this.mapper.write(arguments[0]);
            }
            else {
                result = this.mapper.write(outputStream, arguments[0]);
            }

            return this.conversionService.convert(result, this.method.returnType().type());
        }
    }
}
