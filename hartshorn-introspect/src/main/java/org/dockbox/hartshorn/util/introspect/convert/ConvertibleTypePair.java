package org.dockbox.hartshorn.util.introspect.convert;

public record ConvertibleTypePair(Class<?> sourceType, Class<?> targetType) {

    public ConvertibleTypePair {
        if (sourceType == null) {
            throw new IllegalArgumentException("Source type must not be null");
        }
        if (targetType == null) {
            throw new IllegalArgumentException("Target type must not be null");
        }
    }

    public static ConvertibleTypePair of(final Class<?> sourceType, final Class<?> targetType) {
        return new ConvertibleTypePair(sourceType, targetType);
    }
}
