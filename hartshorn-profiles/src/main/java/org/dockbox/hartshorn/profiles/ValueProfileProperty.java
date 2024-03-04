package org.dockbox.hartshorn.profiles;

import org.dockbox.hartshorn.util.option.Option;

public non-sealed interface ValueProfileProperty extends ProfileProperty {

    Option<String> rawValue();
}
