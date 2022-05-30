/*
 * Copyright 2019-2022 the original author or authors.
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

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.Component;
import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.data.FileFormats;
import org.dockbox.hartshorn.data.mapping.ObjectMapper;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import jakarta.inject.Inject;

@Component
public class DefaultTranslationBundle implements TranslationBundle {

    private final Map<String, Message> messages = new ConcurrentHashMap<>();

    @Inject
    private ApplicationContext applicationContext;
    private Locale primaryLanguage = Locale.getDefault();

    public Locale primaryLanguage() {
        return this.primaryLanguage;
    }

    public DefaultTranslationBundle primaryLanguage(final Locale primaryLanguage) {
        this.primaryLanguage = primaryLanguage;
        return this;
    }

    @Override
    public Set<Message> messages() {
        return Set.copyOf(this.messages.values());
    }

    @Override
    public Result<Message> message(final String key) {
        return this.message(key, this.primaryLanguage());
    }

    @Override
    public Result<Message> message(final String key, final Locale language) {
        return Result.of(this.messages.get(key))
                .map(message -> message.translate(language).detach());
    }

    @Override
    public Message register(final String key, final String value, final Locale language) {
        return this.register(new MessageTemplate(value, key, language));
    }

    @Override
    public Message register(final Message message) {
        this.messages.put(message.key(), this.mergeMessages(message, this.messages));
        return message;
    }

    @Override
    public Message register(final String key, final String value) {
        return this.register(key, value, this.primaryLanguage());
    }

    protected void add(final Message message) {
        message.translate(this.primaryLanguage());
    }

    @Override
    public TranslationBundle merge(final TranslationBundle bundle) {
        final DefaultTranslationBundle translationBundle = new DefaultTranslationBundle().primaryLanguage(this.primaryLanguage());
        final Map<String, Message> messageDict = translationBundle.messages;
        for (final Message message : this.messages()) messageDict.put(message.key(), this.mergeMessages(message, messageDict));
        for (final Message message : bundle.messages()) messageDict.put(message.key(), this.mergeMessages(message, messageDict));
        return translationBundle;
    }

    @Override
    public Set<Message> register(final Map<String, String> messages, final Locale locale) {
        final Set<Message> registeredMessages = new HashSet<>();
        messages.forEach((key, value) -> registeredMessages.add(this.register(key, value, locale)));
        registeredMessages.forEach(this::register);
        return Set.copyOf(registeredMessages);
    }

    @Override
    public Set<Message> register(final Path source, final Locale locale, final FileFormats fileFormat) {
        final ObjectMapper objectMapper = this.applicationContext.get(ObjectMapper.class).fileType(fileFormat);
        final Map<String, String> result = objectMapper.flat(source).entrySet()
                .stream()
                .collect(Collectors.toMap(Entry::getKey, e -> String.valueOf(e.getValue())));
        return this.register(result, locale);
    }

    @Override
    public Set<Message> register(final ResourceBundle resourceBundle) {
        final Map<String, String> result = resourceBundle.keySet().stream()
                .collect(Collectors.toMap(key -> key, resourceBundle::getString));
        return this.register(result, resourceBundle.getLocale());
    }

    private Message mergeMessages(final Message message, final Map<String, Message> messageDict) {
        if (messageDict.containsKey(message.key())) {
            return messageDict.get(message.key()).merge(this.primaryLanguage(), message);
        }
        else return message;
    }
}
