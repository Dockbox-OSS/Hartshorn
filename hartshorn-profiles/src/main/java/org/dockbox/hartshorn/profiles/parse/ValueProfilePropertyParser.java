package org.dockbox.hartshorn.profiles.parse;

import org.dockbox.hartshorn.profiles.CompositeProfileProperty;
import org.dockbox.hartshorn.util.option.Option;

public interface ValueProfilePropertyParser<T> extends ProfilePropertyParser<T> {

    @Override
    default Option<T> parse(CompositeProfileProperty property) {
        return Option.empty();
    }
}
