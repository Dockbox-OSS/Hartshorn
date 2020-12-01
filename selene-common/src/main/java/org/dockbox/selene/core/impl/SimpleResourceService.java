/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.core.impl;

import com.google.inject.Singleton;

import org.dockbox.selene.core.SeleneUtils;
import org.dockbox.selene.core.i18n.common.Language;
import org.dockbox.selene.core.i18n.common.ResourceService;
import org.dockbox.selene.core.i18n.entry.ExternalResourceEntry;
import org.dockbox.selene.core.objects.optional.Exceptional;
import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.files.ConfigurateManager;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@Singleton
public class SimpleResourceService implements ResourceService {

    private final Map<Language, Map<String, String>> resourceMaps = SeleneUtils.emptyConcurrentMap();
    private final List<ExternalResourceEntry> knownEntries = SeleneUtils.emptyConcurrentList();

    @ConfigSerializable
    static class ResourceConfig {
        @Setting
        Map<String, String> translations = SeleneUtils.emptyConcurrentMap();
    }

    @Override
    public void init() {
        this.getResourceMap(Selene.getServer().getGlobalConfig().getDefaultLanguage());
    }

    @SuppressWarnings("ConstantConditions")
    @NotNull
    @Override
    public Map<String, String> getResourceMap(@NotNull Language lang) {
        if (this.resourceMaps.containsKey(lang)) return this.resourceMaps.get(lang);

        ConfigurateManager cm = Selene.getInstance(ConfigurateManager.class);
        Exceptional<ResourceConfig> config = cm.getFileContent(
                cm.getConfigFile(
                        SeleneUtils.getExtension(Selene.class),
                        lang.getCode()
                ), ResourceConfig.class);

        Map<String, String> resources = SeleneUtils.emptyConcurrentMap();
        config.ifPresent(cfg -> resources.putAll(cfg.translations));
        this.resourceMaps.put(lang, resources);
        return resources;
    }

    @NotNull
    @Override
    public Map<Language, String> getTranslations(@NotNull ExternalResourceEntry entry) {
        @NonNls String code = entry.getKey();
        this.knownEntries.add(entry);
        Map<Language, String> resourceMap = SeleneUtils.emptyConcurrentMap();
        this.resourceMaps.forEach((lang, values) -> {
            if (lang.getCode().equals(code)) {
                values.forEach((key, value) -> resourceMap.put(lang, value));
            }
        });
        return resourceMap;
    }

    @NotNull
    @Override
    public String createValidKey(@NotNull String raw) {
        return raw
                .replaceAll("-", "_")
                .replaceAll("\\.", "_")
                .replaceAll("/", "_")
                .toUpperCase();
    }

    @NotNull
    @Override
    public Exceptional<ExternalResourceEntry> getExternalResource(@NotNull String key) {
        @NonNls @NotNull String finalKey = this.createValidKey(key);
        return Exceptional.of(this.knownEntries.stream().filter(entry -> entry.getKey().equals(finalKey)).findFirst());
    }
}
