package org.dockbox.hartshorn.util.introspect.convert;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Set;

public class ConverterAdapter implements GenericConverter, ConditionalConverter {

    private final Converter<?, ?> converter;
    private final ConvertibleTypePair typePair;

    public <I, O> ConverterAdapter(final Class<I> sourceType, final Class<O> targetType, final Converter<I, O> converter) {
        this.converter = converter;
        this.typePair = ConvertibleTypePair.of(sourceType, targetType);
    }

    @Override
    public boolean canConvert(final Object source, final Class<?> targetType) {
        boolean matches = true;
        if (this.converter instanceof ConditionalConverter conditionalConverter) {
            matches = conditionalConverter.canConvert(source, targetType);
        }
        if (matches) {
            if (this.typePair.targetType() != targetType) {
                matches = false;
            }
            else if (!this.typePair.sourceType().isAssignableFrom(source.getClass())) {
                matches = false;
            }
        }
        return matches;
    }

    @Override
    public Set<ConvertibleTypePair> convertibleTypes() {
        return Set.of(this.typePair);
    }

    @SuppressWarnings("unchecked")
    @Override
    public @Nullable <I, O> Object convert(@Nullable final Object source, @NonNull final Class<I> sourceType, @NonNull final Class<O> targetType) {
        final Converter<I, O> ioConverter = (Converter<I, O>) this.converter;
        return ioConverter.convert(sourceType.cast(source));
    }
}
