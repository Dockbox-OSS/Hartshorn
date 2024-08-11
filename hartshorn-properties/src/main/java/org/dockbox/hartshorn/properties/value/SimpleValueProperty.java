package org.dockbox.hartshorn.properties.value;

import org.dockbox.hartshorn.properties.ValueProperty;
import org.dockbox.hartshorn.util.option.Option;

public class SimpleValueProperty implements ValueProperty {

    private final String name;
    private final String value;

    public SimpleValueProperty(String name, String value) {
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

    public <T> Option<T> parse(ValuePropertyParser<T> parser) {
        return parser.parse(this);
    }
}
