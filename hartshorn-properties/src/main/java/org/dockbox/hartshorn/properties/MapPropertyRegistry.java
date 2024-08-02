package org.dockbox.hartshorn.properties;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.dockbox.hartshorn.util.option.Option;

public class MapPropertyRegistry implements PropertyRegistry {

    private final Map<String, ConfiguredProperty> properties = new TreeMap<>();

    @Override
    public List<String> keys() {
        return List.copyOf(properties.keySet());
    }

    @Override
    public Option<ConfiguredProperty> get(String name) {
        ConfiguredProperty property = properties.get(name);
        return Option.of(property);
    }

    @Override
    public void register(ConfiguredProperty property) {
        if (this.contains(property.name())) {
            throw new IllegalArgumentException("Property with name " + property.name() + " already exists. If you intended to load a property with multiple values, implement the appropriate ConfiguredProperty");
        }
        this.properties.put(property.name(), property);
    }

    @Override
    public void unregister(String name) {
        this.properties.remove(name);
    }

    @Override
    public void clear() {
        this.properties.clear();
    }

    @Override
    public boolean contains(String name) {
        return this.properties.containsKey(name);
    }
}
