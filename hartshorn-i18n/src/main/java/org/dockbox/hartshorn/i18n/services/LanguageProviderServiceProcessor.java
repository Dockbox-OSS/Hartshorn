package org.dockbox.hartshorn.i18n.services;

import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.services.ServiceProcessor;
import org.dockbox.hartshorn.i18n.Message;
import org.dockbox.hartshorn.i18n.TranslationBundle;
import org.dockbox.hartshorn.i18n.TranslationService;
import org.dockbox.hartshorn.i18n.annotations.TranslationProvider;
import org.dockbox.hartshorn.i18n.annotations.UseTranslations;

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
                final TranslationBundle bundle = (TranslationBundle) method.invoke(context).rethrow().get();
                translationService.add(bundle);
            }
            else if (method.returnType().childOf(Message.class)) {
                final Message message = (Message) method.invoke(context).rethrow().get();
                translationService.add(message);
            }
        }
    }
}
