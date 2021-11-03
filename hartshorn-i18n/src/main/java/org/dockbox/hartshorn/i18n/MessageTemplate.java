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

import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import lombok.Getter;

public class MessageTemplate implements Message {

    @Getter private Locale language;
    @Getter private final String key;

    private final Map<Locale, Object[]> formattingArgs;
    private final Map<Locale, String> resourceMap;
    private final String defaultValue;

    public MessageTemplate(final String value, final String key, final Locale language, final Object... args) {
        this.language = language;
        this.key = key;

        this.formattingArgs = HartshornUtils.emptyMap();
        this.formattingArgs.put(this.language(), args);

        this.resourceMap = HartshornUtils.emptyMap();
        this.resourceMap.put(language, value);

        this.defaultValue = value;
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

        return ((Message) template).translate(language);
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
        if (0 == args.length) return temp;
        final Map<String, String> map = HartshornUtils.emptyMap();

        for (int i = 0; i < args.length; i++) {
            final String arg = "" + args[i];
            if (arg.isEmpty()) map.put(String.format("{%d}", i), "");
            else map.put(String.format("{%d}", i), arg);
            if (0 == i) map.put("%s", arg);
        }
        return this.replaceFromMap(temp, map);
    }

    private String replaceFromMap(final String string, final Map<String, String> replacements) {
        final StringBuilder sb = new StringBuilder(string);
        int size = string.length();
        for (final Entry<String, String> entry : replacements.entrySet()) {
            if (0 == size) {
                break;
            }
            final String key = entry.getKey();
            final String value = entry.getValue();
            int nextSearchStart;
            int start = sb.indexOf(key, 0);
            while (-1 < start) {
                final int end = start + key.length();
                nextSearchStart = start + value.length();
                sb.replace(start, end, value);
                size -= end - start;
                start = sb.indexOf(key, nextSearchStart);
            }
        }
        return sb.toString();
    }
}
