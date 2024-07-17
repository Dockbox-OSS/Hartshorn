package org.dockbox.hartshorn.properties;

import java.util.List;

import org.dockbox.hartshorn.properties.object.ObjectPropertyParser;
import org.dockbox.hartshorn.util.option.Option;

public non-sealed interface ObjectProperty extends Property {

    List<String> keys();

    Option<ValueProperty> get(String name);

    Option<ObjectProperty> object(String name);

    Option<ListProperty> list(String name);

    <T> Option<T> parse(ObjectPropertyParser<T> parser);
}
