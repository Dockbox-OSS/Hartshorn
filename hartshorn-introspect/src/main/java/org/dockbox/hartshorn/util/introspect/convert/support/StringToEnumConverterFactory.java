package org.dockbox.hartshorn.util.introspect.convert.support;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.util.introspect.convert.Converter;
import org.dockbox.hartshorn.util.introspect.convert.ConverterFactory;

@SuppressWarnings("rawtypes")
public class StringToEnumConverterFactory implements ConverterFactory<String, Enum> {

    @Override
    public <O extends Enum> Converter<String, O> create(final Class<O> targetType) {
        return new StringToEnumConverter<>(targetType);
    }

    @SuppressWarnings("unchecked")
    private static class StringToEnumConverter<T extends Enum> implements Converter<String, T> {

        private final Class<T> enumType;

        private StringToEnumConverter(final Class<T> enumType) {
            this.enumType = enumType;
        }

        @Override
        public @Nullable T convert(final @Nullable String source) {
            assert source != null;
            if (source.isEmpty()) {
                return null;
            }
            try {
                final Enum value = Enum.valueOf(this.enumType, source.trim());
                return this.enumType.cast(value);
            }
            catch (final IllegalArgumentException e) {
                return null;
            }
        }
    }
}
