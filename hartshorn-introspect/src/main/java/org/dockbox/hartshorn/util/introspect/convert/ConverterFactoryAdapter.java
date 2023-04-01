package org.dockbox.hartshorn.util.introspect.convert;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Set;

public class ConverterFactoryAdapter implements GenericConverter, ConditionalConverter {

    private final ConverterFactory<Object, Object> converterFactory;
    private final ConvertibleTypePair typePair;

    public <I, O> ConverterFactoryAdapter(final Class<I> sourceType, final Class<O> targetType, final ConverterFactory<I, O> converterFactory) {
        this.converterFactory = (ConverterFactory<Object, Object>) converterFactory;
        this.typePair = ConvertibleTypePair.of(sourceType, targetType);
    }

    public ConvertibleTypePair typePair() {
        return this.typePair;
    }

    @Override
    public Set<ConvertibleTypePair> convertibleTypes() {
        return null;
    }

    @Override
    public boolean canConvert(final Object source, final Class<?> targetType) {
        boolean matches = true;
        if (!this.typePair.sourceType().isAssignableFrom(source.getClass())) {
            matches = false;
        }
        else if (!this.typePair.targetType().isAssignableFrom(targetType)) {
            // If a factory declares a primitive target, it should only match if the target is declared as a generic Object.
            matches = targetType.isPrimitive() && this.typePair.targetType().equals(Object.class);
        }

        if (matches) {
            if (this.converterFactory instanceof ConditionalConverter conditionalConverter && !conditionalConverter.canConvert(source, targetType)) {
                matches = false;
            }

            final Converter<?, ?> converter = this.converterFactory.create(targetType);
            if (converter instanceof ConditionalConverter conditionalConverter && !conditionalConverter.canConvert(source, targetType)) {
                matches = false;
            }
        }
        return matches;
    }

    @Override
    public @Nullable <I, O> Object convert(@Nullable final Object source, @NonNull final Class<I> sourceType, @NonNull final Class<O> targetType) {
        return this.converterFactory.create(targetType).convert(sourceType.cast(source));
    }
}
