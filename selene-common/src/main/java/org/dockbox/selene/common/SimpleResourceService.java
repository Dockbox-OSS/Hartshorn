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

package org.dockbox.selene.common;

import org.dockbox.selene.api.annotations.entity.Metadata;
import org.dockbox.selene.api.annotations.i18n.Resources;
import org.dockbox.selene.api.files.FileManager;
import org.dockbox.selene.api.i18n.common.Language;
import org.dockbox.selene.api.i18n.common.ResourceEntry;
import org.dockbox.selene.api.i18n.common.ResourceService;
import org.dockbox.selene.api.i18n.entry.Resource;
import org.dockbox.selene.api.objects.Exceptional;
import org.dockbox.selene.api.server.Selene;
import org.dockbox.selene.api.server.SeleneInformation;
import org.dockbox.selene.api.util.Reflect;
import org.dockbox.selene.api.util.SeleneUtils;

import com.google.inject.Singleton;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Singleton
public class SimpleResourceService implements ResourceService {

  private final Map<Language, Map<String, String>> resourceMaps = SeleneUtils.emptyConcurrentMap();
  private final List<ResourceEntry> knownEntries = SeleneUtils.emptyConcurrentList();

  @Override
  public void init() {
    Collection<Class<?>> resourceProviders =
        Reflect.getAnnotatedTypes(SeleneInformation.PACKAGE_PREFIX, Resources.class);
    resourceProviders.forEach(
        provider -> {
          if (provider.isEnum()) {
            for (Enum<?> e : Reflect.getEnumValues(provider)) {
              if (Reflect.isAssignableFrom(ResourceEntry.class, e.getClass())) {
                ResourceEntry resource = (ResourceEntry) e;
                this.knownEntries.add(resource);
              }
            }
          } else {
            for (Field field : Reflect.getStaticFields(provider)) {
              if (Reflect.isAssignableFrom(ResourceEntry.class, field.getType())) {
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
    Path languageConfigFile = cm.getConfigFile(Reflect.getModule(Selene.class), lang.getCode());
    Map<String, String> resources;
    if (languageConfigFile.toFile().exists() && !SeleneUtils.isFileEmpty(languageConfigFile)) {
      resources = SimpleResourceService.getResourcesForFile(languageConfigFile, cm, lang);
    } else {
      resources = this.createDefaultResourceFile(cm, languageConfigFile);
    }
    this.resourceMaps.put(lang, resources);
    return resources;
  }

  @NotNull
  @Override
  public Map<Language, String> getTranslations(@NotNull Resource entry) {
    @NonNls String code = entry.getKey();
    this.knownEntries.add(entry);
    Map<Language, String> resourceMap = SeleneUtils.emptyConcurrentMap();
    for (Language language : Language.values()) {
      Map<String, String> resources =
          this.resourceMaps.getOrDefault(language, SeleneUtils.emptyMap());
      if (resources.containsKey(entry.getKey())) {
        resourceMap.put(language, resources.get(entry.getKey()));
      }
    }
    return resourceMap;
  }

  @NotNull
  @Override
  public String createValidKey(@NotNull String raw) {
    return raw.replaceAll("-", "_").replaceAll("\\.", "_").replaceAll("/", "_").toUpperCase();
  }

  @NotNull
  @Override
  public Exceptional<ResourceEntry> getExternalResource(@NotNull String key) {
    @NonNls
    @NotNull
    String finalKey = this.createValidKey(key);
    return Exceptional.of(
        this.knownEntries.stream().filter(entry -> entry.getKey().equals(finalKey)).findFirst());
  }

  private static Map<String, String> getResourcesForFile(Path file, FileManager cm, Language lang) {
    Exceptional<ResourceConfig> config = cm.read(file, ResourceConfig.class);

    Map<String, String> resources = SeleneUtils.emptyConcurrentMap();
    config.ifPresent(cfg -> resources.putAll(cfg.translations));
    return resources;
  }

  @NotNull
  private Map<String, String> createDefaultResourceFile(FileManager cm, Path languageConfigFile) {
    Map<String, String> resources = SeleneUtils.emptyConcurrentMap();
    this.knownEntries.forEach(resource -> resources.put(resource.getKey(), resource.asString()));
    ResourceConfig config = new ResourceConfig();
    config.translations = resources;
    cm.write(languageConfigFile, config);
    return resources;
  }

  @Metadata(alias = "resources")
  static class ResourceConfig {
    Map<String, String> translations = SeleneUtils.emptyConcurrentMap();
  }
}
