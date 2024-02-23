/*
 * Copyright 2019-2024 the original author or authors.
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

package org.dockbox.hartshorn.i18n;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.dockbox.hartshorn.application.ExceptionHandler;
import org.dockbox.hartshorn.config.FileFormat;
import org.dockbox.hartshorn.config.ObjectMapper;
import org.dockbox.hartshorn.util.CollectionUtilities;
import org.dockbox.hartshorn.util.option.Option;

/**
 * Default implementation of {@link TranslationBundle}. By default, this uses the
 * {@link Locale#getDefault() default locale of the current environment}.
 *
 * @since 0.4.8
 *
 * @author Guus Lieben
 */
public class DefaultTranslationBundle implements TranslationBundle {

    private final Map<String, Message> messages = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;
    private final ExceptionHandler exceptionHandler;

    private Locale primaryLanguage = Locale.getDefault();

    public DefaultTranslationBundle(ObjectMapper objectMapper, ExceptionHandler exceptionHandler) {
        this.objectMapper = objectMapper;
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public Locale primaryLanguage() {
        return this.primaryLanguage;
    }

    @Override
    public DefaultTranslationBundle primaryLanguage(Locale primaryLanguage) {
        this.primaryLanguage = primaryLanguage;
        return this;
    }

    @Override
    public Set<Message> messages() {
        return Set.copyOf(this.messages.values());
    }

    @Override
    public Option<Message> message(String key) {
        return this.message(key, this.primaryLanguage());
    }

    @Override
    public Option<Message> message(String key, Locale language) {
        return Option.of(this.messages.get(key))
                .map(message -> message.translate(language).detach());
    }

    @Override
    public Message register(String key, String value, Locale language) {
        return this.register(new MessageTemplate(value, key, language));
    }

    @Override
    public Message register(Message message) {
        this.messages.put(message.key(), this.mergeMessages(message, this.messages));
        return message;
    }

    @Override
    public Message register(String key, String value) {
        return this.register(key, value, this.primaryLanguage());
    }

    @Override
    public TranslationBundle merge(TranslationBundle bundle) {
        DefaultTranslationBundle translationBundle = new DefaultTranslationBundle(this.objectMapper, this.exceptionHandler)
                .primaryLanguage(this.primaryLanguage());

        Map<String, Message> messageDict = translationBundle.messages;
        CollectionUtilities.forEach(
                message -> messageDict.put(message.key(), this.mergeMessages(message, messageDict)),
                this.messages(),
                bundle.messages()
        );

        return translationBundle;
    }

    @Override
    public Set<Message> register(Map<String, String> messages, Locale locale) {
        Set<Message> registeredMessages = new HashSet<>();
        messages.forEach((key, value) -> registeredMessages.add(this.register(key, value, locale)));
        registeredMessages.forEach(this::register);
        return Set.copyOf(registeredMessages);
    }

    @Override
    public Set<Message> register(Path source, Locale locale, FileFormat fileFormat) {
        ObjectMapper objectMapper = this.objectMapper.fileType(fileFormat);
        try {
            Map<String, String> result = objectMapper.flat(source).entrySet()
                    .stream()
                    .collect(Collectors.toMap(Entry::getKey, value -> String.valueOf(value.getValue())));
            return this.register(result, locale);
        }
        catch (Exception e) {
            this.exceptionHandler.handle(e);
            return Set.of();
        }
    }

    @Override
    public Set<Message> register(ResourceBundle resourceBundle) {
        Map<String, String> result = resourceBundle.keySet().stream()
                .collect(Collectors.toMap(key -> key, resourceBundle::getString));
        return this.register(result, resourceBundle.getLocale());
    }

    private Message mergeMessages(Message message, Map<String, Message> messageDict) {
        if (messageDict.containsKey(message.key())) {
            return messageDict.get(message.key()).merge(this.primaryLanguage(), message);
        }
        else {
            return message;
        }
    }
}
