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
import org.dockbox.hartshorn.component.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.component.processing.proxy.MethodProxyContext;
import org.dockbox.hartshorn.config.annotations.Deserialize;
import org.dockbox.hartshorn.proxy.MethodInterceptor;
import org.dockbox.hartshorn.util.introspect.convert.ConversionService;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

public class DeserializerMethodPostProcessor extends AbstractSerializerPostProcessor<Deserialize> {

    @Override
    public Class<Deserialize> annotation() {
        return Deserialize.class;
    }

    @Override
    public <T, R> MethodInterceptor<T, R> process(final ApplicationContext context, final MethodProxyContext<T> methodContext, final ComponentProcessingContext<T> processingContext) {
        final SerializationSourceConverter converter = this.findConverter(context, methodContext, processingContext);
        //noinspection unchecked
        final MethodView<T, R> method = (MethodView<T, R>) methodContext.method();
        final Deserialize serialize = method.annotations().get(Deserialize.class).get();
        final ObjectMapper mapper = context.get(ObjectMapper.class).fileType(serialize.fileType());
        final TypeView<R> returnType = method.genericReturnType();
        final ConversionService conversionService = context.get(ConversionService.class);

        return new DeserializerMethodInterceptor<>(converter, method, mapper, returnType, conversionService);
    }
}
