package org.dockbox.hartshorn.properties;

import org.dockbox.hartshorn.properties.parse.ConfiguredPropertyParser;
import org.dockbox.hartshorn.util.option.Option;

public class SingleConfiguredProperty implements ConfiguredProperty {

    private final String name;
    private final String value;

    public SingleConfiguredProperty(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public Option<String> value() {
        return Option.of(this.value);
    }

    @Override
    public <T> Option<T> value(ConfiguredPropertyParser<T> parser) {
        return parser.parse(this);
    }
}
