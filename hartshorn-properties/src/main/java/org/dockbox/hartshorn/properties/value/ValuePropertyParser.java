package org.dockbox.hartshorn.properties.value;

import org.dockbox.hartshorn.properties.ValueProperty;
import org.dockbox.hartshorn.util.option.Option;

@FunctionalInterface
public interface ValuePropertyParser<T> {

    Option<T> parse(ValueProperty property);
}
