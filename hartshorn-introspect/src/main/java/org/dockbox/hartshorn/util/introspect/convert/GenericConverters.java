package org.dockbox.hartshorn.util.introspect.convert;

import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.collections.StandardMultiMap.ConcurrentSetMultiMap;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class GenericConverters {

    private final Set<ConditionalConverter> globalConverters = ConcurrentHashMap.newKeySet();
    private final MultiMap<ConvertibleTypePair, GenericConverter> converters = new ConcurrentSetMultiMap<>();

    public void addConverter(final GenericConverter converter) {
        final Set<ConvertibleTypePair> convertibleTypes = converter.convertibleTypes();
        if (convertibleTypes == null) {
            if (converter instanceof ConditionalConverter conditionalConverter) {
                this.globalConverters.add(conditionalConverter);
            }
            else {
                throw new IllegalArgumentException("Converter must implement GenericConditionalConverter if convertibleTypes() returns null");
            }
        }
        else {
            for (final ConvertibleTypePair convertibleType : convertibleTypes) {
                this.converters.put(convertibleType, converter);
            }
        }
    }

    public GenericConverter getConverter(final Class<?> sourceType, final Class<?> targetType) {
        final ConvertibleTypePair pair = new ConvertibleTypePair(sourceType, targetType);
        for (final GenericConverter converter : this.converters.get(pair)) {
            if (converter instanceof final ConditionalConverter conditionalConverter) {
                if (conditionalConverter.canConvert(sourceType, targetType)) {
                    return converter;
                }
            }
            else {
                return converter;
            }
        }
        for (final ConditionalConverter converter : this.globalConverters) {
            if (converter.canConvert(sourceType, targetType)) {
                return (GenericConverter) converter;
            }
        }
        return null;
    }

}
