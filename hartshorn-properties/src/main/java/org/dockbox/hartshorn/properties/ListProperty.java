package org.dockbox.hartshorn.properties;

import java.util.Collection;
import java.util.List;

import org.dockbox.hartshorn.properties.list.ListPropertyParser;

public non-sealed interface ListProperty extends Property {

    List<Property> elements();

    <T> Collection<T> parse(ListPropertyParser<T> parser);
}
