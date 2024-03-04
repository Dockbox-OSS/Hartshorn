package org.dockbox.hartshorn.profiles;

import org.dockbox.hartshorn.profiles.parse.ProfilePropertyParser;
import org.dockbox.hartshorn.util.option.Option;

public final class SimpleProfileProperty implements ValueProfileProperty {

    private final String name;
    private final String rawValue;

    public SimpleProfileProperty(String name, String rawValue) {
        this.name = name;
        this.rawValue = rawValue;
    }

    @Override
    public <T> Option<T> parseValue(ProfilePropertyParser<T> parser) {
        return parser.parse(this);
    }

    @Override
    public <T> T parseValue(ProfilePropertyParser<T> parser, T defaultValue) {
        return parser.parse(this).orElse(defaultValue);
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public Option<String> rawValue() {
        return Option.of(this.rawValue);
    }
}
