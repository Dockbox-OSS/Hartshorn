package org.dockbox.hartshorn.profiles.parse;

import org.dockbox.hartshorn.util.introspect.convert.support.StringToBooleanConverter;
import org.dockbox.hartshorn.util.introspect.convert.support.StringToNumberConverterFactory;

public final class DefaultProfilePropertyParsers {

    private DefaultProfilePropertyParsers() {
        // Utility class
    }

    public static ProfilePropertyParser<Boolean> booleanParser() {
        return new ConverterProfilePropertyParser<>(new StringToBooleanConverter());
    }

    public static ProfilePropertyParser<Integer> integerParser() {
        return new ConverterProfilePropertyParser<>(new StringToNumberConverterFactory().create(Integer.class));
    }

    public static ProfilePropertyParser<Long> longParser() {
        return new ConverterProfilePropertyParser<>(new StringToNumberConverterFactory().create(Long.class));
    }

    public static ProfilePropertyParser<Float> floatParser() {
        return new ConverterProfilePropertyParser<>(new StringToNumberConverterFactory().create(Float.class));
    }

    public static ProfilePropertyParser<Double> doubleParser() {
        return new ConverterProfilePropertyParser<>(new StringToNumberConverterFactory().create(Double.class));
    }
}
