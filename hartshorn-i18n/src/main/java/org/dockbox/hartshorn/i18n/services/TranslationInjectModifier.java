/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.i18n.services;

import org.dockbox.hartshorn.core.HartshornUtils;
import org.dockbox.hartshorn.core.MetaProvider;
import org.dockbox.hartshorn.core.annotations.service.AutomaticActivation;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.domain.TypedOwner;
import org.dockbox.hartshorn.i18n.Message;
import org.dockbox.hartshorn.i18n.TranslationService;
import org.dockbox.hartshorn.i18n.annotations.InjectTranslation;
import org.dockbox.hartshorn.i18n.annotations.UseTranslations;
import org.dockbox.hartshorn.core.proxy.ProxyFunction;
import org.dockbox.hartshorn.core.context.MethodProxyContext;
import org.dockbox.hartshorn.core.services.ServiceAnnotatedMethodModifier;

@AutomaticActivation
public class TranslationInjectModifier extends ServiceAnnotatedMethodModifier<InjectTranslation, UseTranslations> {

    @Override
    public <T, R> ProxyFunction<T, R> process(final ApplicationContext context, final MethodProxyContext<T> methodContext) {
        final String key = this.key(context, methodContext.type(), methodContext.method());
        final InjectTranslation annotation = methodContext.method().annotation(InjectTranslation.class).get();

        return (self, args, holder) -> {
            // Prevents NPE when formatting cached resources without arguments
            final Object[] objects = null == args ? new Object[0] : args;
            return (R) context.get(TranslationService.class).getOrCreate(key, annotation.value()).format(objects);
        };
    }

    @Override
    public <T> boolean preconditions(final ApplicationContext context, final MethodProxyContext<T> methodContext) {
        return methodContext.method().returnType().childOf(Message.class);
    }

    @Override
    public Class<InjectTranslation> annotation() {
        return InjectTranslation.class;
    }

    @Override
    public Class<UseTranslations> activator() {
        return UseTranslations.class;
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
        final Exceptional<InjectTranslation> resource = method.annotation(InjectTranslation.class);
        if (resource.present()) {
            final String key = resource.get().key();
            if (!"".equals(key)) return key;
        }
        String keyJoined = method.name();
        if (keyJoined.startsWith("get")) keyJoined = keyJoined.substring(3);
        final String[] r = HartshornUtils.splitCapitals(keyJoined);
        return prefix + String.join(".", r).toLowerCase();
    }
}
