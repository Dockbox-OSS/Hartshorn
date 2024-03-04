package org.dockbox.hartshorn.profiles.parse.support;

import org.dockbox.hartshorn.profiles.CompositeProfileProperty;
import org.dockbox.hartshorn.profiles.ProfileProperty;
import org.dockbox.hartshorn.profiles.ValueProfileProperty;
import org.dockbox.hartshorn.profiles.parse.ProfilePropertyParser;
import org.dockbox.hartshorn.util.introspect.convert.Converter;
import org.dockbox.hartshorn.util.option.Option;

public class ConverterProfilePropertyParser<T> implements ProfilePropertyParser<T> {

    private final Converter<ProfileProperty, T> converter;

    public ConverterProfilePropertyParser(Converter<ProfileProperty, T> converter) {
        this.converter = converter;
    }

    @Override
    public Option<T> parse(ValueProfileProperty property) {
        return Option.of(this.converter.convert(property));
    }

    @Override
    public Option<T> parse(CompositeProfileProperty property) {
        return Option.of(this.converter.convert(property));
    }
}
