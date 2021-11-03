package org.dockbox.hartshorn.i18n;

import org.dockbox.hartshorn.core.domain.Exceptional;

import java.util.Set;

public interface TranslationBundle {

    Language primaryLanguage();

    TranslationBundle primaryLanguage(Language language);

    Set<Message> messages();

    Exceptional<Message> message(String key);

    Exceptional<Message> message(String key, Language language);

    Message register(String key, String value, Language language);

    Message register(Message message);

    Message register(String key, String value);

    TranslationBundle merge(TranslationBundle bundle);
}
