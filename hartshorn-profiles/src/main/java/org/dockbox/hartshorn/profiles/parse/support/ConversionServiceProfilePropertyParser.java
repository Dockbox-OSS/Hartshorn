package org.dockbox.hartshorn.profiles.parse.support;

import org.dockbox.hartshorn.profiles.CompositeProfileProperty;
import org.dockbox.hartshorn.profiles.ValueProfileProperty;
import org.dockbox.hartshorn.profiles.parse.ProfilePropertyParser;
import org.dockbox.hartshorn.util.introspect.convert.ConversionService;
import org.dockbox.hartshorn.util.option.Option;

public class ConversionServiceProfilePropertyParser<T> implements ProfilePropertyParser<T> {

    private final ConversionService conversionService;
    private final Class<T> type;

    public ConversionServiceProfilePropertyParser(Class<T> type, ConversionService conversionService) {
        this.type = type;
        this.conversionService = conversionService;
    }

    @Override
    public Option<T> parse(ValueProfileProperty property) {
        return property.rawValue().map(rawValue -> {
            Object converted = this.conversionService.convert(rawValue, this.type);
            // Do not use Class#cast here, as it will fail on primitive types
            return (T) converted;
        });
    }

    @Override
    public Option<T> parse(CompositeProfileProperty property) {
        return null;
    }
}
