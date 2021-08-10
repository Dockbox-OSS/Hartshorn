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

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.exceptions.Except;
import org.dockbox.hartshorn.di.annotations.inject.Binds;
import org.dockbox.hartshorn.i18n.common.Language;
import org.dockbox.hartshorn.i18n.common.ResourceEntry;
import org.dockbox.hartshorn.i18n.entry.Resource;
import org.dockbox.hartshorn.util.HartshornUtils;
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
public class ResourceServiceImpl implements ResourceService {

    protected static final Map<Language, ResourceBundle> bundles = HartshornUtils.emptyConcurrentMap();

    public ResourceServiceImpl() {
        if (bundles.isEmpty()) {
            for (final Language language : Language.values()) {
                try {
                    final ResourceBundle bundle = ResourceBundle.getBundle("hartshorn.translations", language.locale());
                    ResourceServiceImpl.bundles.put(language, bundle);
                }
                catch (final Throwable e) {
                    Except.handle(e);
                }
            }
        }
    }

    @NotNull
    @Override
    public Map<String, String> translations(@NotNull final Language lang) {
        final Map<String, String> translations = HartshornUtils.emptyMap();
        if (ResourceServiceImpl.bundles.containsKey(lang)) {
            final ResourceBundle bundle = ResourceServiceImpl.bundles.get(lang);
            final Enumeration<String> keys = bundle.getKeys();
            while (keys.hasMoreElements()) {
                final String key = keys.nextElement();
                try {
                    final String string = bundle.getString(key);
                    translations.put(key, string);
                }
                catch (final MissingResourceException ignored) {
                }
            }
        }
        return translations;
    }

    @NotNull
    @Override
    public Map<Language, String> translations(@NotNull final Resource entry) {
        final Map<Language, String> translations = HartshornUtils.emptyMap();
        for (final Entry<Language, ResourceBundle> bundle : ResourceServiceImpl.bundles.entrySet()) {
            try {
                final String string = bundle.getValue().getString(entry.key());
                translations.put(bundle.getKey(), string);
            }
            catch (final MissingResourceException ignored) {
            }
        }
        return translations;
    }

    @NotNull
    @Override
    public String createValidKey(@NotNull final String raw) {
        // Replace any dashes, underscores, back- and forward slashes with a period. Convert to lowercase
        return raw.replaceAll("[-]|[_]|[/]|[\\\\]", ".").toLowerCase();
    }

    @NotNull
    @Override
    public Exceptional<ResourceEntry> get(@NotNull final String key) {
        return this.resource(key, null, false);
    }

    @Override
    public ResourceEntry getOrCreate(final String key, final String value) {
        return this.resource(key, value, true).get();
    }

    private Exceptional<ResourceEntry> resource(final String key, final String value, final boolean createIfAbsent) {
        @NonNls
        @NotNull final String finalKey = this.createValidKey(key);
        return Exceptional.of(() -> {
            final Map<String, String> translations = this.translations(Language.EN_US);
            if (translations.containsKey(finalKey)) {
                final String knownValue = translations.get(finalKey);
                return new Resource(knownValue, finalKey);
            }
            else {
                if (createIfAbsent && null != value) {
                    return new Resource(value, key);
                }
                else {
                    throw new IllegalStateException("Missing translation for " + finalKey);
                }
            }
        });
    }
}
