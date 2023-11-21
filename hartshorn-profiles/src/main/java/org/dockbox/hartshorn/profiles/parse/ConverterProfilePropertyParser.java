package org.dockbox.hartshorn.profiles.parse;

import org.dockbox.hartshorn.profiles.ProfileProperty;
import org.dockbox.hartshorn.util.introspect.convert.Converter;
import org.dockbox.hartshorn.util.option.Option;

public class ConverterProfilePropertyParser<T> implements ProfilePropertyParser<T> {

    private final Converter<String, T> converter;

    public ConverterProfilePropertyParser(Converter<String, T> converter) {
        this.converter = converter;
    }

    @Override
    public Option<T> parse(ProfileProperty property) {
        String rawValue = property.rawValue();
        return Option.of(this.converter.convert(rawValue));
    }
}
