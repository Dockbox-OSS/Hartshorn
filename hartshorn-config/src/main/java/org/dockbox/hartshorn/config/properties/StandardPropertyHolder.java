/*
 * Copyright 2019-2023 the original author or authors.
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

package org.dockbox.hartshorn.config.properties;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.function.Function;

import org.dockbox.hartshorn.application.ApplicationPropertyHolder;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.config.FileFormats;
import org.dockbox.hartshorn.config.ObjectMapper;
import org.dockbox.hartshorn.util.GenericType;
import org.dockbox.hartshorn.util.StringUtilities;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.option.Option;

import jakarta.inject.Inject;

public class StandardPropertyHolder implements PropertyHolder {

    protected transient Map<String, Object> properties;

    private final ObjectMapper objectMapper;
    private final ObjectMapper propertyMapper;
    private final ApplicationContext applicationContext;

    @Inject
    public StandardPropertyHolder(ApplicationContext applicationContext,
                                  ApplicationPropertyHolder propertyHolder,
                                  ObjectMapper objectMapper,
                                  ObjectMapper propertyMapper) {
        this.applicationContext = applicationContext;
        this.objectMapper = objectMapper.fileType(FileFormats.JSON);
        this.propertyMapper = propertyMapper.fileType(FileFormats.PROPERTIES);
        this.properties = this.createConfigurationMap();
        propertyHolder.properties()
                .forEach((key, value) -> this.set(String.valueOf(key), value));
    }

    @Override
    public boolean has(String key) {
        return this.find(key) != null;
    }

    @Override
    public <T> Option<T> update(T object, String key, Class<T> type) {
        return this.restore(key, serialized -> this.objectMapper.update(object, serialized, type));
    }

    private <T> Option<T> restore(String key, Function<String, Option<T>> mapper) {
        Object value = this.find(key);
        return Option.of(value)
                .flatMap(result -> this.objectMapper.write(result).peekError(this.applicationContext::handle))
                .flatMap(mapper);
    }

    @Override
    public <T> Option<T> update(T object, String key, GenericType<T> type) {
        return Option.of(this.find(key))
                .flatMap(value -> this.objectMapper.write(value).peekError(this.applicationContext::handle))
                .flatMap(serialized -> this.objectMapper.read(serialized, type));
    }

    @Override
    public <T> Option<T> get(String key, Class<T> type) {
        return this.restore(key, serialized -> this.objectMapper.read(serialized, type));
    }

    @Override
    public <T> Option<T> get(String key, GenericType<T> type) {
        return Option.of(this.find(key))
                .flatMap(value -> this.objectMapper.write(value).peekError(this.applicationContext::handle))
                .flatMap(serialized -> this.objectMapper.read(serialized, type));
    }

    @Override
    public void set(Map<String, Object> tree) {
        tree.forEach(this::set);
    }

    @Override
    public <T> void set(String key, T value) {
        String[] split = key.split("\\.");

        Object patch = this.objectMapper.write(value)
                .flatMap(serialized -> this.objectMapper.read(serialized, Map.class))
                .cast(Object.class)
                .orElse(value);

        Map<String, Object> current = this.properties;
        for (int i = 0; i < split.length; i++) {
            String part = split[i];
            if (i == split.length - 1) {
                Object origin = current.get(part);
                if (origin instanceof Map<?, ?> originMap && patch instanceof Map<?, ?> patchMap) {
                    Map<String, Object> adjustedOrigin = TypeUtils.adjustWildcards(originMap, Map.class);
                    Map<String, Object> adjustedPatch = TypeUtils.adjustWildcards(patchMap, Map.class);
                    this.patchConfigurationMap(adjustedOrigin, adjustedPatch);
                    return;
                }
                current.put(part, patch); // Overwrite
                return;
            }
            if (!current.containsKey(part)) {
                current.put(part, this.createConfigurationMap());
            }
            if (!(current.get(part) instanceof Map)) {
                current.put(part, this.createConfigurationMap());
            }
            current = TypeUtils.adjustWildcards((Map<?, ?>) current.get(part), Map.class);
        }
    }

    protected Map<String, Object> createConfigurationMap() {
        return new LinkedHashMap<>();
    }

    protected void patchConfigurationMap(Map<String, Object> origin, Map<String, Object> patch) {
        for (Entry<String, Object> entry : patch.entrySet()) {
            String key = entry.getKey();
            Object patchValue = entry.getValue();
            if (origin.containsKey(key)) {
                Object originValue = origin.get(key);
                if (originValue instanceof Map && patchValue instanceof Map) {
                    this.patchConfigurationMap((Map<String, Object>) originValue, (Map<String, Object>) patchValue);
                }
                else if (originValue instanceof List<?> && patchValue instanceof List<?>) {
                    origin.put(key, this.merge((List<Object>) originValue, (List<Object>) patchValue));
                }
                else {
                    origin.put(key, patchValue);
                }
            }
            else {
                origin.put(key, patchValue);
            }
        }
    }

    protected <T> Collection<T> merge(Collection<T> first, Collection<T> second) {
        second.removeAll(first);
        first.addAll(second);
        return first;
    }

    protected Object find(String key) {
        if (StringUtilities.empty(key)) {
            return this.properties;
        }
        String[] split = key.split("\\.");
        if (split.length == 1) {
            return this.properties.get(key);
        }
        Map<String, Object> current = this.properties;
        for (int i = 0; i < split.length; i++) {
            String part = split[i];
            if (i == split.length - 1) {
                return current.get(part);
            }
            Object next = current.get(part);
            if (next == null) {
                return null;
            }
            if (!(next instanceof Map)) {
                return null;
            }
            current = (Map<String, Object>) next;
        }
        return null;
    }

    @Override
    public Properties properties() {
        Properties properties = new Properties();
        this.propertyMapper.write(this.properties)
                .map(StringReader::new)
                .peek(reader -> {
                    try {
                        properties.load(reader);
                    }
                    catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
        return properties;
    }
}
