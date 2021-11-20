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

import org.dockbox.hartshorn.core.annotations.service.AutomaticActivation;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.services.ServiceProcessor;
import org.dockbox.hartshorn.i18n.Message;
import org.dockbox.hartshorn.i18n.TranslationBundle;
import org.dockbox.hartshorn.i18n.TranslationService;
import org.dockbox.hartshorn.i18n.annotations.TranslationProvider;
import org.dockbox.hartshorn.i18n.annotations.UseTranslations;

@AutomaticActivation
public class LanguageProviderServiceProcessor implements ServiceProcessor<UseTranslations> {

    @Override
    public Class<UseTranslations> activator() {
        return UseTranslations.class;
    }

    @Override
    public boolean preconditions(final ApplicationContext context, final TypeContext<?> type) {
        return !type.methods(TranslationProvider.class).isEmpty();
    }

    @Override
    public <T> void process(final ApplicationContext context, final TypeContext<T> type) {
        final TranslationService translationService = context.get(TranslationService.class);
        for (final MethodContext<?, T> method : type.methods(TranslationProvider.class)) {
            if (method.returnType().childOf(TranslationBundle.class)) {
                final TranslationBundle bundle = (TranslationBundle) method.invoke(context).rethrowUnchecked().get();
                translationService.add(bundle);
            }
            else if (method.returnType().childOf(Message.class)) {
                final Message message = (Message) method.invoke(context).rethrowUnchecked().get();
                translationService.add(message);
            }
        }
    }
}
