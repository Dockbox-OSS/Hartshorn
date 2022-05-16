package org.dockbox.hartshorn.data.config;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.Component;
import org.dockbox.hartshorn.data.FileFormats;
import org.dockbox.hartshorn.data.mapping.ObjectMapper;
import org.dockbox.hartshorn.util.Exceptional;
import org.dockbox.hartshorn.util.GenericType;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.inject.Inject;

@Component(singleton = true)
public class StandardPropertyHolder implements PropertyHolder {

    protected final transient Map<String, Object> properties;

    @Inject
    private ObjectMapper objectMapper;
    private final ObjectMapper propertyMapper;

    @Inject
    public StandardPropertyHolder(final ApplicationContext applicationContext, final ObjectMapper propertyMapper) {
        this.propertyMapper = propertyMapper.fileType(FileFormats.PROPERTIES);
        this.properties = this.createConfigurationMap();
        applicationContext.properties()
                .forEach((k, v) -> this.set(String.valueOf(k), v));
    }

    @Override
    public boolean has(final String key) {
        return this.find(key) != null;
    }

    @Override
    public <T> Exceptional<T> get(final String key, final Class<T> type) {
        final Object value = this.find(key);

        if (type != null) {
            if (type.isInstance(value)) {
                return Exceptional.of(type.cast(value));
            }

            return Exceptional.of(value)
                    .flatMap(object -> this.objectMapper.write(object))
                    .flatMap(serialized -> this.objectMapper.read(serialized, type));
        }
        else {
            return Exceptional.of(value).map(o -> (T) o).map(this::copyOf);
        }
    }

    private <T> T copyOf(final T value) {
        if (value instanceof Collection collection) {
            return (T) new ArrayList<>(collection);
        }
        else if (value instanceof Map map) {
            return (T) new LinkedHashMap<>(map);
        }
        else {
            // Primitive or String
            return value;
        }
    }

    @Override
    public <T> Exceptional<T> get(final String key, final GenericType<T> type) {
        return Exceptional.of(this.find(key))
                .flatMap(value -> this.objectMapper.write(value))
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
                .map(map -> (Object) map)
                .or(value);

        Map<String, Object> current = this.properties;
        for (int i = 0; i < split.length; i++) {
            final String part = split[i];
            if (i == split.length - 1) {
                final Object origin = current.get(part);
                if (origin instanceof Map map && patch instanceof Map) {
                    this.patchConfigurationMap(map, (Map) patch);
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
            current = (Map<String, Object>) current.get(part);
        }
    }

    protected Map<String, Object> createConfigurationMap() {
        return new LinkedHashMap<>();
    }

    protected void patchConfigurationMap(final Map<String, Object> origin, final Map<String, Object> patch) {
        for(final String key : patch.keySet()) {
            final Object patchValue = patch.get(key);
            if (origin.containsKey(key)) {
                final Object originValue = origin.get(key);
                if (originValue instanceof Map && patchValue instanceof Map)
                    this.patchConfigurationMap((Map<String, Object>) originValue, (Map<String, Object>) patchValue);
                else if (originValue instanceof Collection<?> && patchValue instanceof Collection<?>)
                    origin.put(key, this.merge((List) originValue, (List) patchValue));
                else origin.put(key, patchValue);
            } else origin.put(key, patchValue);
        }
    }

    protected Collection<?> merge(final Collection first, final Collection second) {
        second.removeAll(first);
        first.addAll(second);
        return first;
    }

    protected Object find(final String key) {
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
                .present(reader -> {
                    try {
                        properties.load(reader);
                    } catch (final IOException e) {
                        throw new RuntimeException(e);
                    }
                });
        return properties;
    }
}
