package org.dockbox.hartshorn.profiles.parse;

import org.dockbox.hartshorn.profiles.parse.support.ConverterValueProfilePropertyParser;
import org.dockbox.hartshorn.util.introspect.convert.support.StringToBooleanConverter;
import org.dockbox.hartshorn.util.introspect.convert.support.StringToNumberConverterFactory;

public final class DefaultProfilePropertyParsers {

    private DefaultProfilePropertyParsers() {
        // Utility class
    }

    public static ValueProfilePropertyParser<Boolean> booleanParser() {
        return new ConverterValueProfilePropertyParser<>(new StringToBooleanConverter());
    }

    public static ProfilePropertyParser<Integer> integerParser() {
        return new ConverterValueProfilePropertyParser<>(new StringToNumberConverterFactory().create(Integer.class));
    }

    public static ProfilePropertyParser<Long> longParser() {
        return new ConverterValueProfilePropertyParser<>(new StringToNumberConverterFactory().create(Long.class));
    }

    public static ProfilePropertyParser<Float> floatParser() {
        return new ConverterValueProfilePropertyParser<>(new StringToNumberConverterFactory().create(Float.class));
    }

    public static ProfilePropertyParser<Double> doubleParser() {
        return new ConverterValueProfilePropertyParser<>(new StringToNumberConverterFactory().create(Double.class));
    }
}
