package org.dockbox.hartshorn.properties.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.dockbox.hartshorn.properties.ListProperty;
import org.dockbox.hartshorn.properties.Property;
import org.dockbox.hartshorn.properties.ValueProperty;
import org.dockbox.hartshorn.properties.value.ValuePropertyParser;

public class ObjectListPropertyParser<T> implements ListPropertyParser<T> {

    private final ValuePropertyParser<T> delegate;

    public ObjectListPropertyParser(ValuePropertyParser<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Collection<T> parse(ListProperty property) {
        List<T> values = new ArrayList<>();
        for(Property element : property.elements()) {
            if (element instanceof ValueProperty valueProperty) {
                values.add(this.delegate.parse(valueProperty).orNull());
            }
            else {
                throw new IllegalArgumentException("Expected ValueProperty, got " + element.getClass().getSimpleName());
            }
        }
        return values;
    }
}
