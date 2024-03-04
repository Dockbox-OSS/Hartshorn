package org.dockbox.hartshorn.profiles.parse.support;

import org.dockbox.hartshorn.profiles.CompositeProfileProperty;
import org.dockbox.hartshorn.profiles.ValueProfileProperty;
import org.dockbox.hartshorn.profiles.parse.ProfilePropertyParser;
import org.dockbox.hartshorn.util.introspect.convert.ConvertibleTypePair;
import org.dockbox.hartshorn.util.introspect.convert.GenericConverter;
import org.dockbox.hartshorn.util.option.Option;

public class GenericConverterProfilePropertyParser<T> implements ProfilePropertyParser<T> {

    private final GenericConverter converter;
    private final Class<T> type;

    public GenericConverterProfilePropertyParser(Class<T> type, GenericConverter converter) {
        this.type = type;
        boolean compatible = false;
        for(ConvertibleTypePair convertibleType : converter.convertibleTypes()) {
            if (type.isAssignableFrom(convertibleType.targetType())) {
                compatible = true;
                break;
            }
        }
        if (!compatible) {
            throw new IllegalArgumentException("Converter is not compatible with type " + type.getName());
        }
        this.converter = converter;
    }

    @Override
    public Option<T> parse(ValueProfileProperty property) {
        return property.rawValue().map(rawValue -> {
            Object converted = this.converter.convert(rawValue, String.class, this.type);
            // Do not use Class#cast here, as it will fail on primitive types
            return (T) converted;
        });
    }

    @Override
    public Option<T> parse(CompositeProfileProperty property) {
        return null;
    }
}
