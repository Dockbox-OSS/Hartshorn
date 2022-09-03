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

import org.dockbox.hartshorn.util.StringUtilities;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MessageTemplate implements Message {

    private Locale language;
    private final String key;

    private final Map<Locale, Object[]> formattingArgs;
    private final Map<Locale, String> resourceMap;
    private final String defaultValue;

    public MessageTemplate(final String value, final String key, final Locale language, final Object... args) {
        this.language = language;
        this.key = key;

        this.formattingArgs = new HashMap<>();
        this.formattingArgs.put(this.language(), args);

        this.resourceMap = new HashMap<>();
        this.resourceMap.put(language, value);

        this.defaultValue = value;
    }

    @Override
    public Locale language() {
        return this.language;
    }

    @Override
    public String key() {
        return this.key;
    }

    @Override
    public Message merge(final Locale language, final Message message) {
        if (!this.key().equals(message.key()))
            throw new IllegalArgumentException("Key of provided message does not match existing message key, expected '" + this.key() + "' but received '" + message.key() + "'");

        final MessageTemplate template = new MessageTemplate(this.safeValue(), this.key, this.language);
        template.resourceMap.putAll(this.resourceMap);
        template.formattingArgs.putAll(this.formattingArgs);

        if (message instanceof MessageTemplate messageTemplate) {
            template.resourceMap.putAll(messageTemplate.resourceMap);
            template.formattingArgs.putAll(messageTemplate.formattingArgs);
        }

        return template.translate(language);
    }

    @Override
    public Message detach() {
        final MessageTemplate template = new MessageTemplate(this.defaultValue, this.key(), this.language());
        template.resourceMap.putAll(this.resourceMap);
        template.formattingArgs.putAll(this.formattingArgs);
        return template;
    }

    protected String safeValue() {
        return this.resourceMap.getOrDefault(this.language(), this.defaultValue);
    }

    @Override
    public Message translate(final MessageReceiver receiver) {
        return this.translate(receiver.language());
    }

    @Override
    public Message translate(final Locale lang) {
        this.language = lang;
        return this;
    }

    @Override
    public Message format(final Object... args) {
        return this.format(this.language(), args);
    }

    @Override
    public Message format(final Locale language, final Object... args) {
        this.formattingArgs.put(language, args);
        return this;
    }

    @Override
    public String string() {
        final String temp = this.safeValue();
        final Object[] args = this.formattingArgs.getOrDefault(this.language(), new Object[0]);
        return StringUtilities.format(temp, args);
    }
}
