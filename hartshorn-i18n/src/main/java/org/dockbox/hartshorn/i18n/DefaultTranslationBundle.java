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

package org.dockbox.hartshorn.i18n;

import org.dockbox.hartshorn.core.HartshornUtils;
import org.dockbox.hartshorn.core.annotations.inject.Binds;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.data.FileFormats;
import org.dockbox.hartshorn.data.mapping.ObjectMapper;

import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import lombok.Getter;
import lombok.Setter;

@Binds(TranslationBundle.class)
public class DefaultTranslationBundle implements TranslationBundle {

    @Inject
    private ApplicationContext applicationContext;

    @Getter @Setter
    private Locale primaryLanguage = Locale.getDefault();

    private final Map<String, Message> messages = HartshornUtils.emptyConcurrentMap();

    @Override
    public Set<Message> messages() {
        return HartshornUtils.asUnmodifiableSet(this.messages.values());
    }

    @Override
    public Exceptional<Message> message(final String key) {
        return this.message(key, this.primaryLanguage());
    }

    @Override
    public Exceptional<Message> message(final String key, final Locale language) {
        return Exceptional.of(this.messages.get(key))
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
        final Set<Message> registeredMessages = HartshornUtils.emptySet();
        messages.forEach((key, value) -> registeredMessages.add(this.register(key, value, locale)));
        registeredMessages.forEach(this::register);
        return HartshornUtils.asUnmodifiableSet(registeredMessages);
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
