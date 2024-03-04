package org.dockbox.hartshorn.profiles.parse;

import org.dockbox.hartshorn.profiles.CompositeProfileProperty;
import org.dockbox.hartshorn.profiles.ValueProfileProperty;
import org.dockbox.hartshorn.util.option.Option;

public interface ProfilePropertyParser<T> {

    Option<T> parse(ValueProfileProperty property);

    Option<T> parse(CompositeProfileProperty property);
}
