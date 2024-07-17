package org.dockbox.hartshorn.properties.list;

import java.util.Collection;
import java.util.List;

import org.dockbox.hartshorn.properties.ListProperty;
import org.dockbox.hartshorn.properties.Property;

public record SimpleListProperty(String name, List<Property> elements) implements ListProperty {

    @Override
    public List<Property> elements() {
        return List.copyOf(this.elements);
    }

    @Override
    public <T> Collection<T> parse(ListPropertyParser<T> parser) {
        return parser.parse(this);
    }
}
