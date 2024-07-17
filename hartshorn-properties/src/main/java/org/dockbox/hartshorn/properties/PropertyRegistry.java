package org.dockbox.hartshorn.properties;

import java.util.List;

import org.dockbox.hartshorn.properties.parse.ConfiguredPropertyParser;
import org.dockbox.hartshorn.util.option.Option;

public interface PropertyRegistry {

    List<String> keys();

    Option<ConfiguredProperty> get(String name);

    default Option<String> value(String name) {
        return this.get(name).flatMap(ConfiguredProperty::value);
    }

    default <T> Option<T> value(String name, ConfiguredPropertyParser<T> parser) {
        return this.get(name).flatMap(parser::parse);
    }

    void register(ConfiguredProperty property);

    void unregister(String name);

    void clear();

    boolean contains(String name);
}
