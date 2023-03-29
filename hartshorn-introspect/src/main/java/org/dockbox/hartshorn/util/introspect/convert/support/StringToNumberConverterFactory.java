package org.dockbox.hartshorn.util.introspect.convert.support;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.util.introspect.convert.Converter;
import org.dockbox.hartshorn.util.introspect.convert.ConverterFactory;

import java.util.function.Function;

public class StringToNumberConverterFactory implements ConverterFactory<String, Number> {

    @Override
    public <O extends Number> Converter<String, O> create(final Class<O> targetType) {
        final Converter<String, ? extends Number> converter;
        if (targetType == Integer.class) {
            converter = new StringToNumberConverter<>(Integer::parseInt, Integer::decode);
        }
        else if (targetType == Long.class) {
            converter = new StringToNumberConverter<>(Long::parseLong, Long::decode);
        }
        else if (targetType == Float.class) {
            converter = new StringToNumberConverter<>(Float::parseFloat, Float::valueOf);
        }
        else if (targetType == Double.class) {
            converter = new StringToNumberConverter<>(Double::parseDouble, Double::valueOf);
        }
        else if (targetType == Short.class) {
            converter = new StringToNumberConverter<>(Short::parseShort, Short::decode);
        }
        else if (targetType == Byte.class) {
            converter = new StringToNumberConverter<>(Byte::parseByte, Byte::decode);
        }
        else {
            converter = null;
        }
        //noinspection unchecked
        return (Converter<String, O>) converter;
    }

    private static class StringToNumberConverter<T extends Number> implements Converter<String, T> {

        private final Function<String, T> parseFunction;
        private final Function<String, T> decodeFunction;

        public StringToNumberConverter(final Function<String, T> parseFunction, final Function<String, T> decodeFunction) {
            this.parseFunction = parseFunction;
            this.decodeFunction = decodeFunction;
        }

        @Override
        public @Nullable T convert(final @Nullable String input) {
            assert input != null;
            try {
                if (isHexNumber(input)) {
                    return this.decodeFunction.apply(input);
                }
                else {
                    return this.parseFunction.apply(input);
                }
            }
            catch (final NumberFormatException e) {
                // If primitive, the conversion service will default to zero
                return null;
            }
        }
    }

    public static boolean isHexNumber(final String value) {
        final int index = value.startsWith("-") ? 1 : 0;
        return value.toLowerCase().startsWith("0x", index) || value.startsWith("#", index);
    }
}
