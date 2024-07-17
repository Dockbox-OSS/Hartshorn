package org.dockbox.hartshorn.properties.object;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dockbox.hartshorn.properties.ListProperty;
import org.dockbox.hartshorn.properties.ObjectProperty;
import org.dockbox.hartshorn.properties.Property;
import org.dockbox.hartshorn.properties.ValueProperty;
import org.dockbox.hartshorn.util.option.Option;

public class SimpleObjectProperty implements ObjectProperty {

    private final String name;
    private final Map<String, Property> properties;

    public SimpleObjectProperty(String name, Map<String, Property> properties) {
        this.name = name;
        this.properties = new HashMap<>(properties);
    }

    @Override
    public List<String> keys() {
        return List.copyOf(this.properties.keySet());
    }

    @Override
    public Option<ValueProperty> get(String name) {
        return Option.of(this.properties.get(name)).ofType(ValueProperty.class);
    }

    @Override
    public Option<ObjectProperty> object(String name) {
        return Option.of(this.properties.get(name)).ofType(ObjectProperty.class);
    }

    @Override
    public Option<ListProperty> list(String name) {
        return Option.of(this.properties.get(name)).ofType(ListProperty.class);
    }

    @Override
    public <T> Option<T> parse(ObjectPropertyParser<T> parser) {
        return parser.parse(this);
    }

    @Override
    public String name() {
        return this.name;
    }
}
