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

package org.dockbox.hartshorn.i18n.services;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.i18n.Message;
import org.dockbox.hartshorn.i18n.TranslationService;
import org.dockbox.hartshorn.i18n.annotations.InjectTranslation;
import org.dockbox.hartshorn.i18n.annotations.UseTranslations;
import org.dockbox.hartshorn.inject.MetaProvider;
import org.dockbox.hartshorn.inject.TypedOwner;
import org.dockbox.hartshorn.proxy.MethodInterceptor;
import org.dockbox.hartshorn.proxy.processing.MethodProxyContext;
import org.dockbox.hartshorn.proxy.processing.ServiceAnnotatedMethodInterceptorPostProcessor;
import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.util.StringUtilities;
import org.dockbox.hartshorn.util.reflect.MethodContext;
import org.dockbox.hartshorn.util.reflect.TypeContext;

public class TranslationInjectPostProcessor extends ServiceAnnotatedMethodInterceptorPostProcessor<InjectTranslation, UseTranslations> {

    @Override
    public <T, R> MethodInterceptor<T> process(final ApplicationContext context, final MethodProxyContext<T> methodContext, final ComponentProcessingContext processingContext) {
        final String key = this.key(context, methodContext.type(), methodContext.method());
        final InjectTranslation annotation = methodContext.method().annotation(InjectTranslation.class).get();

        return interceptorContext -> {
            // Prevents NPE when formatting cached resources without arguments
            final Object[] args = interceptorContext.args();
            final Object[] objects = null == args ? new Object[0] : args;
            return (R) context.get(TranslationService.class).getOrCreate(key, annotation.value()).format(objects);
        };
    }

    @Override
    public <T> boolean preconditions(final ApplicationContext context, final MethodProxyContext<T> methodContext, final ComponentProcessingContext processingContext) {
        return methodContext.method().returnType().childOf(Message.class);
    }

    @Override
    public Class<InjectTranslation> annotation() {
        return InjectTranslation.class;
    }

    protected String key(final ApplicationContext context, final TypeContext<?> type, final MethodContext<?, ?> method) {
        String prefix = "";

        final MetaProvider provider = context.meta();
        if (provider.isComponent(type)) {
            final TypedOwner lookup = provider.lookup(type);
            if (lookup != null) prefix = lookup.id() + '.';
        }

        final String extracted = this.extract(method, prefix);
        context.log().debug("Determined I18N key for %s: %s".formatted(method.qualifiedName(), extracted));
        return extracted;
    }

    protected String extract(final MethodContext<?, ?> method, final String prefix) {
        final Result<InjectTranslation> resource = method.annotation(InjectTranslation.class);
        if (resource.present()) {
            final String key = resource.get().key();
            if (!"".equals(key)) return key;
        }
        String keyJoined = method.name();
        if (keyJoined.startsWith("get")) keyJoined = keyJoined.substring(3);
        final String[] r = StringUtilities.splitCapitals(keyJoined);
        return prefix + String.join(".", r).toLowerCase();
    }
}
