package org.dockbox.hartshorn.properties;

import org.dockbox.hartshorn.util.option.Option;

public non-sealed interface ValueProperty extends Property {

    Option<String> value();
}
