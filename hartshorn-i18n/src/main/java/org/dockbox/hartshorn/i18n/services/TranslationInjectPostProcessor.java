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

package org.dockbox.hartshorn.i18n.services;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.i18n.Message;
import org.dockbox.hartshorn.i18n.annotations.InjectTranslation;
import org.dockbox.hartshorn.proxy.advice.intercept.MethodInterceptor;
import org.dockbox.hartshorn.component.processing.proxy.MethodProxyContext;
import org.dockbox.hartshorn.component.processing.proxy.ServiceAnnotatedMethodInterceptorPostProcessor;
import org.dockbox.hartshorn.util.introspect.convert.ConversionService;

public class TranslationInjectPostProcessor extends ServiceAnnotatedMethodInterceptorPostProcessor<InjectTranslation> {

    public static final Object[] EMPTY_ARGS = new Object[0];

    @Override
    public <T, R> MethodInterceptor<T, R> process(ApplicationContext context, MethodProxyContext<T> methodContext, ComponentProcessingContext<T> processingContext) {
        String key = context.get(TranslationKeyGenerator.class).key(methodContext.type(), methodContext.method());
        context.log().debug("Determined I18N key for %s: %s".formatted(methodContext.method().qualifiedName(), key));

        InjectTranslation annotation = methodContext.method().annotations().get(InjectTranslation.class).get();
        ConversionService conversionService = context.get(ConversionService.class);

        return new TranslationInjectMethodInterceptor<>(context, key, annotation, conversionService, methodContext);
    }

    @Override
    public <T> boolean preconditions(ApplicationContext context, MethodProxyContext<T> methodContext, ComponentProcessingContext<T> processingContext) {
        return methodContext.method().returnType().isChildOf(Message.class);
    }

    @Override
    public Class<InjectTranslation> annotation() {
        return InjectTranslation.class;
    }
}
