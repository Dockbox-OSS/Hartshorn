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

import org.dockbox.hartshorn.proxy.advice.intercept.MethodInterceptor;
import org.dockbox.hartshorn.proxy.advice.intercept.MethodInterceptorContext;
import org.dockbox.hartshorn.util.introspect.convert.ConversionService;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

import java.io.InputStream;

/**
 * TODO: #1062 Add documentation
 *
 * @param <T> ...
 * @param <R> ...
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class DeserializerMethodInterceptor<T, R> implements MethodInterceptor<T, R> {
    private final SerializationSourceConverter converter;
    private final MethodView<T, R> method;
    private final ObjectMapper mapper;
    private final TypeView<R> returnType;
    private final ConversionService conversionService;

    public DeserializerMethodInterceptor(SerializationSourceConverter converter, MethodView<T, R> method,
                                         ObjectMapper mapper, TypeView<R> returnType,
                                         ConversionService conversionService) {
        this.converter = converter;
        this.method = method;
        this.mapper = mapper;
        this.returnType = returnType;
        this.conversionService = conversionService;
    }

    @Override
    public R intercept(MethodInterceptorContext<T, R> interceptorContext) throws Throwable {
        try (InputStream inputStream = this.converter.inputStream(this.method, interceptorContext.args())) {
            Option<?> result = this.mapper.read(inputStream, this.returnType.type());
            return this.conversionService.convert(result, this.returnType.type());
        }
    }
}
