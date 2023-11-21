package org.dockbox.hartshorn.profiles;

import org.dockbox.hartshorn.profiles.parse.ProfilePropertyParser;
import org.dockbox.hartshorn.util.option.Option;

public record SimpleProfileProperty(String name, String rawValue) implements ProfileProperty {

    @Override
    public <T> Option<T> parseValue(ProfilePropertyParser<T> parser) {
        return parser.parse(this);
    }

    @Override
    public <T> T parseValue(ProfilePropertyParser<T> parser, T defaultValue) {
        return parser.parse(this).orElse(defaultValue);
    }
}
