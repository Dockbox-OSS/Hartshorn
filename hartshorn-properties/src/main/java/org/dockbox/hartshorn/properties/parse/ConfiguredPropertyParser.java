package org.dockbox.hartshorn.properties.parse;

import org.dockbox.hartshorn.properties.ConfiguredProperty;
import org.dockbox.hartshorn.util.option.Option;

@FunctionalInterface
public interface ConfiguredPropertyParser<T> {

    Option<T> parse(ConfiguredProperty property);

}
