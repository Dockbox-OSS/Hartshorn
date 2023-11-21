package org.dockbox.hartshorn.profiles.parse;

import org.dockbox.hartshorn.profiles.ProfileProperty;
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
    public Option<T> parse(ProfileProperty property) {
        String rawValue = property.rawValue();
        Object converted = this.converter.convert(rawValue, String.class, this.type);
        return Option.of(converted).cast(this.type);
    }
}
