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

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.component.processing.ProcessingPriority;
import org.dockbox.hartshorn.component.processing.proxy.MethodProxyContext;
import org.dockbox.hartshorn.config.annotations.Serialize;
import org.dockbox.hartshorn.proxy.advice.intercept.MethodInterceptor;
import org.dockbox.hartshorn.util.introspect.TypeParameterList;
import org.dockbox.hartshorn.util.introspect.convert.ConversionService;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeParameterView;

/**
 * TODO: #1062 Add documentation
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public class SerializerMethodPostProcessor extends AbstractSerializerPostProcessor<Serialize> {

    @Override
    public Class<Serialize> annotation() {
        return Serialize.class;
    }

    @Override
    public <T, R> MethodInterceptor<T, R> process(ApplicationContext context, MethodProxyContext<T> methodContext, ComponentProcessingContext<T> processingContext) {
        SerializationSourceConverter converter = this.findConverter(context, methodContext, processingContext);
        //noinspection unchecked
        MethodView<T, R> method = (MethodView<T, R>) methodContext.method();
        Serialize serialize = method.annotations().get(Serialize.class).get();
        ObjectMapper mapper = context.get(ObjectMapper.class).fileType(serialize.fileType());
        boolean returnsStringOrWrapper = this.returnsStringOrWrapper(method);
        ConversionService conversionService = context.get(ConversionService.class);

        return new SerializerMethodInterceptor<>(converter, method, returnsStringOrWrapper, mapper, conversionService);
    }

    private boolean returnsStringOrWrapper(MethodView<?, ?> method) {
        if (method.returnType().is(String.class)) {
            return true;
        }
        TypeParameterList typeParameters = method.genericReturnType().typeParameters().allInput();
        return typeParameters.count() == 1 && typeParameters.atIndex(0)
                .flatMap(TypeParameterView::resolvedType)
                .test(type -> type.is(String.class));
    }

    @Override
    public int priority() {
        return ProcessingPriority.NORMAL_PRECEDENCE;
    }
}
