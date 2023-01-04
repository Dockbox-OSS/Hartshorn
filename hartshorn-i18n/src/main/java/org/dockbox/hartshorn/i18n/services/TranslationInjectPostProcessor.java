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
import org.dockbox.hartshorn.component.ComponentContainer;
import org.dockbox.hartshorn.component.ComponentLocator;
import org.dockbox.hartshorn.component.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.i18n.Message;
import org.dockbox.hartshorn.i18n.TranslationService;
import org.dockbox.hartshorn.i18n.annotations.InjectTranslation;
import org.dockbox.hartshorn.proxy.MethodInterceptor;
import org.dockbox.hartshorn.proxy.processing.MethodProxyContext;
import org.dockbox.hartshorn.proxy.processing.ServiceAnnotatedMethodInterceptorPostProcessor;
import org.dockbox.hartshorn.util.StringUtilities;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

public class TranslationInjectPostProcessor extends ServiceAnnotatedMethodInterceptorPostProcessor<InjectTranslation> {

    @Override
    public <T, R> MethodInterceptor<T, R> process(final ApplicationContext context, final MethodProxyContext<T> methodContext, final ComponentProcessingContext<T> processingContext) {
        final String key = this.key(context, methodContext.type(), methodContext.method());
        final InjectTranslation annotation = methodContext.method().annotations().get(InjectTranslation.class).get();

        return interceptorContext -> {
            // Prevents NPE when formatting cached resources without arguments
            final Object[] args = interceptorContext.args();
            final Object[] objects = null == args ? new Object[0] : args;
            return interceptorContext.checkedCast(context.get(TranslationService.class).getOrCreate(key, annotation.value()).format(objects));
        };
    }

    @Override
    public <T> boolean preconditions(final ApplicationContext context, final MethodProxyContext<T> methodContext, final ComponentProcessingContext<T> processingContext) {
        return methodContext.method().returnType().isChildOf(Message.class);
    }

    @Override
    public Class<InjectTranslation> annotation() {
        return InjectTranslation.class;
    }

    protected String key(final ApplicationContext context, final TypeView<?> type, final MethodView<?, ?> method) {
        final String i18nKey = this.createI18nKey(context, method);
        context.log().debug("Determined I18N key for %s: %s".formatted(method.qualifiedName(), i18nKey));
        return i18nKey;
    }

    protected String createI18nKey(final ApplicationContext context, final MethodView<?, ?> method) {
        final Option<InjectTranslation> resource = method.annotations().get(InjectTranslation.class);

        // If the method has an explicit key, use that without further processing
        if (resource.present()) {
            final String resourceKey = resource.get().key();
            if (StringUtilities.notEmpty(resourceKey))
                return resourceKey;
        }

        String methodName = method.name();
        if (methodName.startsWith("get")) methodName = methodName.substring(3);
        String methodKey = String.join(".", StringUtilities.splitCapitals(methodName)).toLowerCase();

        final TypeView<?> declaringType = method.declaredBy();
        final Option<ComponentContainer> container = context.get(ComponentLocator.class).container(declaringType.type());
        if (container.present()) {
            final String containerKey = container.get().id();
            if (containerKey != null) methodKey = containerKey + "." + methodKey;
        }

        return methodKey;
    }
}
