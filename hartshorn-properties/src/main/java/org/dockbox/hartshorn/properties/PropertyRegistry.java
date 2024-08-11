package org.dockbox.hartshorn.properties;

import java.util.List;
import java.util.function.Function;

import org.dockbox.hartshorn.properties.value.ValuePropertyParser;
import org.dockbox.hartshorn.util.option.Option;

public interface PropertyRegistry {

    List<String> keys();

    Option<ValueProperty> get(String name);

    Option<ObjectProperty> object(String name);

    Option<ListProperty> list(String name);

    // mapper used if value is (e.g.) a,b,c instead of 'proper' list
    Option<ListProperty> list(String name, Function<ValueProperty, ListProperty> singleValueMapper);

    default Option<String> value(String name) {
        return this.get(name).flatMap(ValueProperty::value);
    }

    default <T> Option<T> value(String name, ValuePropertyParser<T> parser) {
        return this.get(name).flatMap(parser::parse);
    }

    void register(ValueProperty property);

    void unregister(String name);

    void clear();

    boolean contains(String name);
}
