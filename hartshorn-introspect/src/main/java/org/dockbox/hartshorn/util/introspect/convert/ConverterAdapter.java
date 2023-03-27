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
        if (this.typePair.targetType() != targetType) return false;
        if (this.typePair.sourceType().isAssignableFrom(source.getClass())) {
            if (this.converter instanceof ConditionalConverter conditionalConverter) {
                return conditionalConverter.canConvert(source, targetType);
            }
            return true;
        }
        return false;
    }

    @Override
    public Set<ConvertibleTypePair> convertibleTypes() {
        return Set.of(this.typePair);
    }

    @SuppressWarnings("unchecked")
    @Override
    public @Nullable <I, O> Object convert(@NonNull final Object source, @NonNull final Class<I> sourceType, @NonNull final Class<O> targetType) {
        if (source == null) {
            return null;
        }
        final Converter<I, O> ioConverter = (Converter<I, O>) this.converter;
        return ioConverter.convert(sourceType.cast(source));
    }
}
