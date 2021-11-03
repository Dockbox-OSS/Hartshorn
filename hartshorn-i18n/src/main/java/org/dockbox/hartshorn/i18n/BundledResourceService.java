package org.dockbox.hartshorn.i18n;

import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.domain.Exceptional;

import java.util.Map;

import javax.inject.Inject;

import lombok.Getter;

public class BundledResourceService implements ResourceService {

    @Inject
    @Getter
    private ApplicationContext applicationContext;

    @Inject
    private TranslationBundle translationBundle;

    @Inject
    private Language defaultLanguage;

    @Override
    public Map<String, String> translations(final Language lang) {
        return null;
    }

    @Override
    public Map<Language, String> translations(final MessageTemplate entry) {
        return null;
    }

    @Override
    public String createValidKey(final String raw) {
        return null;
    }

    @Override
    public Exceptional<Message> get(final String key) {
        return this.translationBundle.message(key);
    }

    @Override
    public Message getOrCreate(final String key, final String value) {
        return this.translationBundle.message(key)
                .orElse(() -> this.translationBundle.register(key, value))
                .get();
    }
}
