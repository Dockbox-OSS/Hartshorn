package org.dockbox.hartshorn.properties.list;

import java.util.Collection;

import org.dockbox.hartshorn.properties.ListProperty;

public interface ListPropertyParser<T> {

    Collection<T> parse(ListProperty property);
}
