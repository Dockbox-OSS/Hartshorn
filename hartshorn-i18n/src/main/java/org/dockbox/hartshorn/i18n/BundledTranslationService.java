package org.dockbox.hartshorn.i18n;

import org.dockbox.hartshorn.core.annotations.inject.Binds;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.domain.Exceptional;

import javax.inject.Inject;

import lombok.Getter;

@Binds(TranslationService.class)
public class BundledTranslationService implements TranslationService {

    @Inject
    @Getter
    private ApplicationContext applicationContext;

    @Inject
    @Getter
    private TranslationBundle bundle;

    @Override
    public Exceptional<Message> get(final String key) {
        return this.bundle.message(this.clean(key));
    }

    @Override
    public Message getOrCreate(final String key, final String value) {
        return this.bundle.message(this.clean(key))
                .orElse(() -> this.bundle.register(key, value))
                .get();
    }

    @Override
    public void add(final TranslationBundle bundle) {
        this.bundle = this.bundle.merge(bundle);
    }

    @Override
    public void add(final Message message) {
        this.bundle.register(message);
    }

    private String clean(final String key) {
        return key.replaceAll("[/\\\\_-]", ".");
    }
}
