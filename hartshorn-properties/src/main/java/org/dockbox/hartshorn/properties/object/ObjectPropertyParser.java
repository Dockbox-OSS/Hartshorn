package org.dockbox.hartshorn.properties.object;

import org.dockbox.hartshorn.properties.ObjectProperty;
import org.dockbox.hartshorn.util.option.Option;

public interface ObjectPropertyParser<T> {

    Option<T> parse(ObjectProperty property);
}
