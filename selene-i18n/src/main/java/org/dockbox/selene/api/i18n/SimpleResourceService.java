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

package org.dockbox.selene.api.i18n;

import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.api.exceptions.Except;
import org.dockbox.selene.api.i18n.common.Language;
import org.dockbox.selene.api.i18n.common.ResourceEntry;
import org.dockbox.selene.api.i18n.entry.Resource;
import org.dockbox.selene.di.annotations.Binds;
import org.dockbox.selene.util.SeleneUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Enumeration;
import java.util.Map;
import java.util.Map.Entry;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.inject.Singleton;

@Singleton
@Binds(ResourceService.class)
public class SimpleResourceService implements ResourceService {

    private static final Map<Language, ResourceBundle> bundles = SeleneUtils.emptyConcurrentMap();

    @Override
    public void init() {
        for (Language language : Language.values()) {
            try {
                ResourceBundle bundle = ResourceBundle.getBundle("selene.translations", language.getLocale());
                SimpleResourceService.bundles.put(language, bundle);
            } catch (Throwable e) {
                Except.handle(e);
            }
        }
    }

    @NotNull
    @Override
    public Map<String, String> translations(@NotNull Language lang) {
        Map<String, String> translations = SeleneUtils.emptyMap();
        if (SimpleResourceService.bundles.containsKey(lang)) {
            ResourceBundle bundle = SimpleResourceService.bundles.get(lang);
            Enumeration<String> keys = bundle.getKeys();
            while (keys.hasMoreElements()) {
                String key = keys.nextElement();
                try {
                    String string = bundle.getString(key);
                    translations.put(key, string);
                } catch (MissingResourceException ignored) {
                }
            }
        }
        return translations;
    }

    @NotNull
    @Override
    public Map<Language, String> translations(@NotNull Resource entry) {
        Map<Language, String> translations = SeleneUtils.emptyMap();
        for (Entry<Language, ResourceBundle> bundle : SimpleResourceService.bundles.entrySet()) {
            try {
                String string = bundle.getValue().getString(entry.getKey());
                translations.put(bundle.getKey(), string);
            } catch (MissingResourceException ignored) {
            }
        }
        return translations;
    }

    @NotNull
    @Override
    public String createValidKey(@NotNull String raw) {
        // Replace any dashes, underscores, back- and forward slashes with a period. Convert to lowercase
        return raw.replaceAll("[-]|[_]|[/]|[\\\\]", ".").toLowerCase();
    }

    @NotNull
    @Override
    public Exceptional<ResourceEntry> get(@NotNull String key) {
        return this.resource(key, null, false);
    }

    @Override
    public ResourceEntry getOrCreate(String key, String value) {
        return this.resource(key, value, true).get();
    }

    private Exceptional<ResourceEntry> resource(String key, String value, boolean createIfAbsent) {
        @NonNls
        @NotNull
        String finalKey = this.createValidKey(key);
        return Exceptional.of(() -> {
            Map<String, String> translations = this.translations(Language.EN_US);
            if (translations.containsKey(finalKey)) {
                String knownValue = translations.get(finalKey);
                return new Resource(knownValue, finalKey);
            } else {
                if (createIfAbsent && null != value) {
                    return new Resource(value, key);
                } else {
                    throw new IllegalStateException("Missing translation for " + finalKey);
                }
            }
        });
    }
}
