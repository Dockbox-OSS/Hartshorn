package org.dockbox.hartshorn.i18n;

import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.persistence.FileType;

import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

public interface TranslationBundle {

    Locale primaryLanguage();

    TranslationBundle primaryLanguage(Locale language);

    Set<Message> messages();

    Exceptional<Message> message(String key);

    Exceptional<Message> message(String key, Locale language);

    TranslationBundle merge(TranslationBundle bundle);

    Message register(String key, String value, Locale language);

    Message register(Message message);

    Message register(String key, String value);

    Set<Message> register(Map<String, String> messages, Locale locale);

    Set<Message> register(Path source, Locale locale, FileType fileType);

    Set<Message> register(ResourceBundle resourceBundle);
}
