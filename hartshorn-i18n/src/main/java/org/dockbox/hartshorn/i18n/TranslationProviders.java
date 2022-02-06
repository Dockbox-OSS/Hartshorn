package org.dockbox.hartshorn.i18n;

import org.dockbox.hartshorn.core.annotations.inject.Provider;
import org.dockbox.hartshorn.core.annotations.stereotype.Service;
import org.dockbox.hartshorn.i18n.annotations.UseTranslations;

@Service(activators = UseTranslations.class)
public class TranslationProviders {

    @Provider
    public TranslationService translationService() {
        return new BundledTranslationService();
    }

    @Provider
    public TranslationBundle translationBundle() {
        return new DefaultTranslationBundle();
    }
}
