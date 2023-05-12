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
import org.dockbox.hartshorn.component.ComponentPopulator;
import org.dockbox.hartshorn.component.ComponentPostConstructor;
import org.dockbox.hartshorn.component.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.component.processing.ProcessingOrder;
import org.dockbox.hartshorn.inject.Enable;
import org.dockbox.hartshorn.proxy.MethodInterceptor;
import org.dockbox.hartshorn.component.processing.proxy.MethodProxyContext;
import org.dockbox.hartshorn.component.processing.proxy.ServiceAnnotatedMethodInterceptorPostProcessor;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.introspect.convert.ConversionService;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.option.Option;

public class FactoryServicePostProcessor extends ServiceAnnotatedMethodInterceptorPostProcessor<Factory> {

    @Override
    public Class<Factory> annotation() {
        return Factory.class;
    }

    @Override
    public <T> boolean preconditions(final ApplicationContext context, final MethodProxyContext<T> methodContext, final ComponentProcessingContext<T> processingContext) {
        return !methodContext.method().returnType().isVoid();
    }

    @Override
    public <T, R> MethodInterceptor<T, R> process(final ApplicationContext context, final MethodProxyContext<T> methodContext, final ComponentProcessingContext<T> processingContext) {
        //noinspection unchecked
        final MethodView<T, R> method = (MethodView<T, R>) methodContext.method();
        final ConversionService conversionService = context.get(ConversionService.class);
        final boolean enable = Boolean.TRUE.equals(method.annotations().get(Enable.class).map(Enable::value).orElse(true));
        if (method.modifiers().isAbstract()) {
            final FactoryContext factoryContext = context.first(FactoryContext.class).get();

            final Option<? extends ConstructorView<?>> constructorCandidate = factoryContext.get(method);
            if (constructorCandidate.present()) {
                final ConstructorView<?> constructor = constructorCandidate.get();
                return interceptorContext -> {
                    final Object instance = constructor.create(interceptorContext.args()).rethrow().orNull();
                    final R result = conversionService.convert(instance, method.returnType().type());
                    return this.processInstance(context, result, enable);
                };
            }
            else {
                final Factory factory = method.annotations().get(Factory.class).get();
                if (factory.required()) {
                    throw new MissingFactoryConstructorException(processingContext.key(), method);
                }
                else {
                    return interceptorContext -> null;
                }
            }
        }
        else {
            return interceptorContext -> {
                final T instance = interceptorContext.instance();
                final Object result = methodContext.method().invoke(instance, interceptorContext.args()).orNull();
                final R convertedResult = conversionService.convert(result, method.returnType().type());
                return this.processInstance(context, convertedResult, enable);
            };
        }
    }

    private <T> T processInstance(final ApplicationContext context, final T instance, final boolean enable) throws ApplicationException {
        T out = instance;
        out = context.get(ComponentPopulator.class).populate(out);
        if (enable) {
            out = context.get(ComponentPostConstructor.class).doPostConstruct(out);
        }
        return out;
    }

    @Override
    public Integer order() {
        return ProcessingOrder.LAST;
    }
}
