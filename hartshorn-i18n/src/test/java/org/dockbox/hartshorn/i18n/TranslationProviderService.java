package org.dockbox.hartshorn.i18n;

import org.dockbox.hartshorn.core.annotations.service.Service;
import org.dockbox.hartshorn.core.boot.Hartshorn;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.i18n.annotations.TranslationProvider;
import org.dockbox.hartshorn.persistence.FileType;

import java.nio.file.Path;
import java.util.Locale;

@Service
public class TranslationProviderService {

    @TranslationProvider
    public TranslationBundle dutch(final ApplicationContext context) {
        final TranslationBundle bundle = context.get(TranslationBundle.class);
        bundle.primaryLanguage(new Locale("nl", "NL"));
        bundle.register("lang.name", "Nederlands");
        return bundle;
    }

    @TranslationProvider
    public TranslationBundle english(final ApplicationContext context) {
        final TranslationBundle bundle = context.get(TranslationBundle.class);
        final Path path = Hartshorn.resource("i18n/en_us.yml").get();
        bundle.register(path, Locale.US, FileType.YAML);
        return bundle;
    }
}
