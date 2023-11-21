package org.dockbox.hartshorn.profiles.parse;

import org.dockbox.hartshorn.profiles.ProfileProperty;
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
    public Option<T> parse(ProfileProperty property) {
        String rawValue = property.rawValue();
        Object converted = this.conversionService.convert(rawValue, this.type);
        // Do not use cast here, as it will fail on primitive types
        return Option.of((T) converted);
    }
}
