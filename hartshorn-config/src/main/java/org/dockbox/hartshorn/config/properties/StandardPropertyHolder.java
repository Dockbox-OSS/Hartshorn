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

import org.dockbox.hartshorn.application.ApplicationPropertyHolder;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.config.FileFormats;
import org.dockbox.hartshorn.config.ObjectMapper;
import org.dockbox.hartshorn.util.GenericType;
import org.dockbox.hartshorn.util.StringUtilities;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.option.Option;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.function.Function;

import jakarta.inject.Inject;

public class StandardPropertyHolder implements PropertyHolder {

    protected final transient Map<String, Object> properties;

    private final ObjectMapper objectMapper;
    private final ObjectMapper propertyMapper;
    private final ApplicationContext applicationContext;

    @Inject
    public StandardPropertyHolder(final ApplicationContext applicationContext,
                                  final ApplicationPropertyHolder propertyHolder,
                                  final ObjectMapper objectMapper,
                                  final ObjectMapper propertyMapper) {
        this.applicationContext = applicationContext;
        this.objectMapper = objectMapper.fileType(FileFormats.JSON);
        this.propertyMapper = propertyMapper.fileType(FileFormats.PROPERTIES);
        this.properties = this.createConfigurationMap();
        propertyHolder.properties()
                .forEach((key, value) -> this.set(String.valueOf(key), value));
    }

    @Override
    public boolean has(final String key) {
        return this.find(key) != null;
    }

    @Override
    public <T> Option<T> update(final T object, final String key, final Class<T> type) {
        return this.restore(key, serialized -> this.objectMapper.update(object, serialized, type));
    }

    private <T> Option<T> restore(final String key, final Function<String, Option<T>> mapper) {
        final Object value = this.find(key);
        return Option.of(value)
                .flatMap(result -> this.objectMapper.write(result).peekError(this.applicationContext::handle))
                .flatMap(mapper);
    }

    @Override
    public <T> Option<T> update(final T object, final String key, final GenericType<T> type) {
        return Option.of(this.find(key))
                .flatMap(value -> this.objectMapper.write(value).peekError(this.applicationContext::handle))
                .flatMap(serialized -> this.objectMapper.read(serialized, type));
    }

    @Override
    public <T> Option<T> get(final String key, final Class<T> type) {
        return this.restore(key, serialized -> this.objectMapper.read(serialized, type));
    }

    private <T> T copyOf(final T value) {
        if (value instanceof Collection<?> collection) {
            return (T) new ArrayList<>(collection);
        }
        else if (value instanceof Map<?, ?> map) {
            return (T) new LinkedHashMap<>(map);
        }
        else {
            // Primitive or String
            return value;
        }
    }

    @Override
    public <T> Option<T> get(final String key, final GenericType<T> type) {
        return Option.of(this.find(key))
                .flatMap(value -> this.objectMapper.write(value).peekError(this.applicationContext::handle))
                .flatMap(serialized -> this.objectMapper.read(serialized, type));
    }

    @Override
    public void set(final Map<String, Object> tree) {
        tree.forEach(this::set);
    }

    @Override
    public <T> void set(final String key, final T value) {
        final String[] split = key.split("\\.");

        final Object patch = this.objectMapper.write(value)
                .flatMap(serialized -> this.objectMapper.read(serialized, Map.class))
                .cast(Object.class)
                .orElse(value);

        Map<String, Object> current = this.properties;
        for (int i = 0; i < split.length; i++) {
            final String part = split[i];
            if (i == split.length - 1) {
                final Object origin = current.get(part);
                if (origin instanceof Map && patch instanceof Map) {
                    final Map<String, Object> adjustedOrigin = TypeUtils.adjustWildcards(origin, Map.class);
                    final Map<String, Object> adjustedPatch = TypeUtils.adjustWildcards(patch, Map.class);
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
            current = TypeUtils.adjustWildcards(current.get(part), Map.class);
        }
    }

    protected Map<String, Object> createConfigurationMap() {
        return new LinkedHashMap<>();
    }

    protected void patchConfigurationMap(final Map<String, Object> origin, final Map<String, Object> patch) {
        for (final Entry<String, Object> entry : patch.entrySet()) {
            final String key = entry.getKey();
            final Object patchValue = entry.getValue();
            if (origin.containsKey(key)) {
                final Object originValue = origin.get(key);
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
            else origin.put(key, patchValue);
        }
    }

    protected <T> Collection<T> merge(final Collection<T> first, final Collection<T> second) {
        second.removeAll(first);
        first.addAll(second);
        return first;
    }

    protected Object find(final String key) {
        if (StringUtilities.empty(key)) {
            return this.properties;
        }
        final String[] split = key.split("\\.");
        if (split.length == 1) {
            return this.properties.get(key);
        }
        Map<String, Object> current = this.properties;
        for (int i = 0; i < split.length; i++) {
            final String part = split[i];
            if (i == split.length - 1) {
                return current.get(part);
            }
            final Object next = current.get(part);
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
        final Properties properties = new Properties();
        this.propertyMapper.write(this.properties)
                .map(StringReader::new)
                .peek(reader -> {
                    try {
                        properties.load(reader);
                    }
                    catch (final IOException e) {
                        throw new RuntimeException(e);
                    }
                });
        return properties;
    }
}
