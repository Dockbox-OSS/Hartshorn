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

package org.dockbox.hartshorn.component.factory;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.proxy.advice.intercept.MethodInterceptorContext;
import org.dockbox.hartshorn.util.introspect.convert.ConversionService;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.MethodView;

/**
 * @deprecated See {@link Factory}.
 */
@Deprecated(since = "23.1", forRemoval = true)
public class ConstructorFactoryAbstractMethodInterceptor<T, R> extends ConstructorFactoryMethodInterceptor<T, R> {
    private final ConstructorView<?> constructor;
    private final ConversionService conversionService;
    private final MethodView<T, R> method;
    private final ApplicationContext context;
    private final boolean enable;

    public ConstructorFactoryAbstractMethodInterceptor(final ConstructorView<?> constructor,
                                                       final ConversionService conversionService,
                                                       final MethodView<T, R> method, final ApplicationContext context,
                                                       final boolean enable) {
        this.constructor = constructor;
        this.conversionService = conversionService;
        this.method = method;
        this.context = context;
        this.enable = enable;
    }

    @Override
    public R intercept(final MethodInterceptorContext<T, R> interceptorContext) throws Throwable {
        final Object instance = this.constructor.create(interceptorContext.args()).rethrow().orNull();
        final R result = this.conversionService.convert(instance, this.method.returnType().type());
        return this.processInstance(this.context, result, this.enable);
    }
}
