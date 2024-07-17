package org.dockbox.hartshorn.properties;

import org.dockbox.hartshorn.properties.parse.ConfiguredPropertyParser;
import org.dockbox.hartshorn.util.option.Option;

public interface ConfiguredProperty {

    String name();

    Option<String> value();

    <T> Option<T> value(ConfiguredPropertyParser<T> parser);
}
