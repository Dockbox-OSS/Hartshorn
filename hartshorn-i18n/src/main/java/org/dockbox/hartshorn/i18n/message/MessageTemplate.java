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

package org.dockbox.hartshorn.i18n.message;

import org.dockbox.hartshorn.core.context.ContextCarrier;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.i18n.MessageReceiver;
import org.dockbox.hartshorn.i18n.ResourceService;
import org.dockbox.hartshorn.i18n.common.Language;
import org.dockbox.hartshorn.i18n.common.Languages;
import org.dockbox.hartshorn.i18n.common.Message;
import org.dockbox.hartshorn.i18n.text.Text;
import org.dockbox.hartshorn.core.HartshornUtils;

import java.util.Map;

import lombok.Getter;

public class MessageTemplate implements Message, ContextCarrier {

    @Getter private final Language language;
    private final Object[] formattingArgs;
    @Getter private final String key;
    private final Map<Language, String> resourceMap;
    private final String value;
    @Getter private final ApplicationContext applicationContext;
    @Getter private final MessageFormatting formatting;

    public MessageTemplate(final ApplicationContext context, final String value, final String key) {
        this(context, value, key, Languages.EN_US);
    }

    public MessageTemplate(final ApplicationContext context, final String value, final String key, final Language language) {
        this(context, value, key, language, new Object[0]);
    }

    public MessageTemplate(final ApplicationContext context, final String value, final String key, final Language language, final Object... args) {
        this.key = key;
        this.applicationContext = context;
        this.resourceMap = context.get(ResourceService.class).translations(this);
        this.value = this.resourceMap.getOrDefault(language, value);
        this.language = language;
        this.formattingArgs = args;
        this.formatting = context.get(MessageFormatting.class);
    }

    @Override
    public Text asText() {
        return Text.of(this.asString());
    }

    @Override
    public String asString() {
        return this.formatCustom();
    }

    @Override
    public String plain() {
        return this.formatCustom().replaceAll("[$|&][0-9a-fklmnor]", "");
    }

    @Override
    public Message translate(final MessageReceiver receiver) {
        return this.translate(receiver.language());
    }

    @Override
    public Message translate(final Language lang) {
        if (this.resourceMap.containsKey(lang))
            return new MessageTemplate(this.applicationContext, this.resourceMap.get(lang), this.key(), lang, this.formattingArgs);
        return this;
    }

    @Override
    public Message translate() {
        return this.translate(Languages.EN_US);
    }

    @Override
    public Message format(final Object... args) {
        return new MessageTemplate(this.applicationContext, this.value, this.key, this.language, args);
    }

    private String formatCustom() {
        String temp = this.value;
        if (0 == this.formattingArgs.length) return temp;
        final Map<String, String> map = HartshornUtils.emptyMap();

        for (int i = 0; i < this.formattingArgs.length; i++) {
            final String arg = "" + this.formattingArgs[i];
            if (arg.isEmpty()) map.put(String.format("{%d}", i), "");
            else map.put(String.format("{%d}", i), arg);
            if (0 == i) map.put("%s", arg);
        }
        temp = this.replaceFromMap(temp, map);
        return this.parseColors(temp);
    }

    protected String parseColors(final String m) {
        String temp = m;
        final char[] nativeFormats = "abcdef1234567890klmnor".toCharArray();
        for (final char c : nativeFormats)
            temp = temp.replace(String.format("&%s", c), String.format("\u00A7%s", c));
        return temp
                .replace("$1", java.lang.String.format("\u00A7%s", this.formatting.primary()))
                .replace("$2", java.lang.String.format("\u00A7%s", this.formatting.secondary()))
                .replace("$3", java.lang.String.format("\u00A7%s", this.formatting.minor()))
                .replace("$4", java.lang.String.format("\u00A7%s", this.formatting.error()));
    }
}
