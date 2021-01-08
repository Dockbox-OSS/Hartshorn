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

package org.dockbox.selene.core.impl;

import com.google.inject.Singleton;

import org.dockbox.selene.core.annotations.i18n.Resources;
import org.dockbox.selene.core.files.FileManager;
import org.dockbox.selene.core.i18n.common.Language;
import org.dockbox.selene.core.i18n.common.ResourceEntry;
import org.dockbox.selene.core.i18n.common.ResourceService;
import org.dockbox.selene.core.i18n.entry.Resource;
import org.dockbox.selene.core.objects.Exceptional;
import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.server.SeleneInformation;
import org.dockbox.selene.core.util.SeleneUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;


@Singleton
public class SimpleResourceService implements ResourceService {

    private final Map<Language, Map<String, String>> resourceMaps = SeleneUtils.COLLECTION.emptyConcurrentMap();
    private final List<ResourceEntry> knownEntries = SeleneUtils.COLLECTION.emptyConcurrentList();

    @ConfigSerializable
    static class ResourceConfig {
        @Setting
        Map<String, String> translations = SeleneUtils.COLLECTION.emptyConcurrentMap();
    }

    @Override
    public void init() {
        Collection<Class<?>> resourceProviders = SeleneUtils.REFLECTION.getAnnotatedTypes(SeleneInformation.PACKAGE_PREFIX, Resources.class);
        resourceProviders.forEach(provider -> {
            if (provider.isEnum()) {
                for (Enum<?> e : SeleneUtils.REFLECTION.getEnumValues(provider)) {
                    if (SeleneUtils.REFLECTION.isAssignableFrom(ResourceEntry.class, e.getClass())) {
                        ResourceEntry resource = (ResourceEntry) e;
                        this.knownEntries.add(resource);
                    }
                }
            } else {
                for (Field field : SeleneUtils.REFLECTION.getStaticFields(provider)) {
                    if (SeleneUtils.REFLECTION.isAssignableFrom(ResourceEntry.class, field.getType())) {
                        try {
                            if (!field.isAccessible()) field.setAccessible(true);
                            ResourceEntry resource = (ResourceEntry) field.get(null);
                            this.knownEntries.add(resource);
                        } catch (IllegalAccessException e) {
                            Selene.handle("Could not access static resource", e);
                        }
                    }
                }
            }
        });
        this.getResourceMap(Selene.getServer().getGlobalConfig().getDefaultLanguage());
    }

    @NotNull
    @Override
    public Map<String, String> getResourceMap(@NotNull Language lang) {
        if (this.resourceMaps.containsKey(lang)) return this.resourceMaps.get(lang);

        FileManager cm = Selene.provide(FileManager.class);
        Path languageConfigFile = cm.getConfigFile(
                SeleneUtils.REFLECTION.getExtension(Selene.class),
                lang.getCode()
        );
        Map<String, String> resources;
        if (languageConfigFile.toFile().exists() && !SeleneUtils.OTHER.isFileEmpty(languageConfigFile)) {
            resources = this.getResourcesForFile(languageConfigFile, cm, lang);
        } else {
            resources = this.createDefaultResourceFile(cm, languageConfigFile);
        }
        this.resourceMaps.put(lang, resources);
        return resources;
    }

    @NotNull
    private Map<String, String> createDefaultResourceFile(FileManager cm, Path languageConfigFile) {
        Map<String, String> resources = SeleneUtils.COLLECTION.emptyConcurrentMap();
        this.knownEntries.forEach(resource -> {
            resources.put(resource.getKey(), resource.asString());
        });
        ResourceConfig config = new ResourceConfig();
        config.translations = resources;
        cm.writeFileContent(languageConfigFile, config);
        return resources;
    }

    private Map<String, String> getResourcesForFile(Path file, FileManager cm, Language lang) {
        Exceptional<ResourceConfig> config = cm.getFileContent(file, ResourceConfig.class);

        Map<String, String> resources = SeleneUtils.COLLECTION.emptyConcurrentMap();
        config.ifPresent(cfg -> resources.putAll(cfg.translations));
        return resources;
    }

    @NotNull
    @Override
    public Map<Language, String> getTranslations(@NotNull Resource entry) {
        @NonNls String code = entry.getKey();
        this.knownEntries.add(entry);
        Map<Language, String> resourceMap = SeleneUtils.COLLECTION.emptyConcurrentMap();
        for (Language language : Language.values()) {
            Map<String, String> resources = this.resourceMaps.getOrDefault(language, SeleneUtils.COLLECTION.emptyMap());
            if (resources.containsKey(entry.getKey())) {
                resourceMap.put(language, resources.get(entry.getKey()));
            }
        }
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
    public Exceptional<ResourceEntry> getExternalResource(@NotNull String key) {
        @NonNls @NotNull String finalKey = this.createValidKey(key);
        return Exceptional.of(this.knownEntries.stream().filter(entry -> entry.getKey().equals(finalKey)).findFirst());
    }
}
