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

package org.dockbox.hartshorn.i18n.entry;

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.i18n.MessageReceiver;
import org.dockbox.hartshorn.i18n.ResourceService;
import org.dockbox.hartshorn.i18n.common.Language;
import org.dockbox.hartshorn.i18n.common.ResourceEntry;
import org.dockbox.hartshorn.i18n.text.Text;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.util.Map;

import lombok.Getter;

public class Resource implements ResourceEntry {

    @Getter private final Language language;
    private final Object[] formattingArgs;
    @Getter private final String key;
    private final Map<Language, String> resourceMap;
    private final String value;

    public Resource(String value, String key) {
        this(value, key, Language.EN_US);
    }

    public Resource(String value, String key, Language language) {
        this(value, key, language, new Object[0]);
    }

    public Resource(String value, String key, Language language, Object... args) {
        this.key = key;
        this.resourceMap = Hartshorn.context().get(ResourceService.class).translations(this);
        this.value = this.resourceMap.getOrDefault(language, value);
        this.language = language;
        this.formattingArgs = args;
    }

    @Override
    public ResourceEntry translate(Language lang) {
        if (this.resourceMap.containsKey(lang))
            return new Resource(this.resourceMap.get(lang), this.key(), lang, this.formattingArgs);
        return this;
    }

    @Override
    public ResourceEntry translate(MessageReceiver receiver) {
        return this.translate(receiver.language());
    }

    @Override
    public ResourceEntry translate() {
        return this.translate(Language.EN_US);
    }

    @Override
    public Text asText() {
        return Text.of(this.asString());
    }

    @Override
    public ResourceEntry format(Object... args) {
        return new Resource(this.value, this.key, this.language, args);
    }

    @Override
    public String asString() {
        return this.formatCustom();
    }

    @Override
    public String plain() {
        return this.formatCustom().replaceAll("[$|&][0-9a-fklmnor]", "");
    }

    private String formatCustom() {
        String temp = this.value;
        if (0 == this.formattingArgs.length) return temp;
        Map<String, String> map = HartshornUtils.emptyMap();

        for (int i = 0; i < this.formattingArgs.length; i++) {
            String arg = "" + this.formattingArgs[i];
            if (arg.isEmpty()) map.put(String.format("{%d}", i), "");
            else map.put(String.format("{%d}", i), arg);
            if (0 == i) map.put("%s", arg);
        }
        temp = this.replaceFromMap(temp, map);
        return Resource.parseColors(temp);
    }

    public static String parseColors(String m) {
        String temp = m;
        char[] nativeFormats = "abcdef1234567890klmnor".toCharArray();
        for (char c : nativeFormats)
            temp = temp.replace(String.format("&%s", c), String.format("\u00A7%s", c));
        return temp
                .replace("$1", java.lang.String.format("\u00A7%s", ResourceColors.primary()))
                .replace("$2", java.lang.String.format("\u00A7%s", ResourceColors.secondary()))
                .replace("$3", java.lang.String.format("\u00A7%s", ResourceColors.minor()))
                .replace("$4", java.lang.String.format("\u00A7%s", ResourceColors.error()));
    }
}
