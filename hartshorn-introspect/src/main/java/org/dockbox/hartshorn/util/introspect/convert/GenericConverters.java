package org.dockbox.hartshorn.util.introspect.convert;

import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.collections.StandardMultiMap.ConcurrentSetMultiMap;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class GenericConverters {

    private final Set<ConditionalConverter> globalConverters = ConcurrentHashMap.newKeySet();
    private final MultiMap<ConvertibleTypePair, GenericConverter> converters = new ConcurrentSetMultiMap<>();

    public void addConverter(final GenericConverter converter) {
        final Set<ConvertibleTypePair> convertibleTypes = converter.convertibleTypes();
        if (convertibleTypes == null) {
            if (converter instanceof final ConditionalConverter conditionalConverter) {
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

    public GenericConverter getConverter(final Object source, final Class<?> targetType) {
        GenericConverter converter = this.getTypeMatchingConverter(source, targetType);
        if (converter == null) {
            converter = this.getGlobalConverter(source, targetType);
        }
        if (converter == null) {
            converter = this.getClosestMatchingConverter(source, targetType);
        }
        return converter;
    }

    private GenericConverter getClosestMatchingConverter(final Object source, final Class<?> targetType) {
        DistantConverterMatch<ConvertibleTypePair> bestMatch = null;
        for (final ConvertibleTypePair typePair : this.converters.keySet()) {
            Class<?> inputType = source.getClass();
            Class<?> sourceType = typePair.targetType();
            if (inputType.isArray() && sourceType.isArray()) {
                inputType = inputType.getComponentType();
                sourceType = sourceType.getComponentType();
            }

            // Recursive solution to iterate super classes first, then interfaces
            if (typePair.sourceType().isAssignableFrom(source.getClass()) && typePair.targetType().isAssignableFrom(targetType)) {
                // distance is the amount of classes in the hierarchy between the typePair sourceType and the source class
                // the closer the distance, the more specific the typePair sourceType is
                // the more specific the typePair sourceType is, the more likely it is that the converter can convert the source
                // to the target type
                final int distance = this.hierarchyDistance(source.getClass(), typePair.sourceType());
                if (distance >= 0) {
                    if (bestMatch == null || distance < bestMatch.distance()) {
                        bestMatch = new DistantConverterMatch<>(typePair, distance);
                    }
                }
            }
        }
        if (bestMatch != null) {
            return this.getConverterForPair(source, targetType, bestMatch.target());
        }
        return null;
    }

    private int hierarchyDistance(Class<?> inputType, Class<?> sourceType) {
        if (inputType == sourceType) {
            return 0;
        }

        int interfaceDistance = -1;
        for (final Class<?> interfaceType : inputType.getInterfaces()) {
            if (sourceType.isAssignableFrom(interfaceType)) {
                final int distance = this.hierarchyDistance(interfaceType, sourceType);
                if (distance >= 0) {
                    interfaceDistance = distance;
                }
            }
        }

        int classDistance = -1;
        final Class<?> superClass = inputType.getSuperclass();
        if (superClass != null) {
            if (sourceType.isAssignableFrom(superClass)) {
                classDistance = this.hierarchyDistance(superClass, sourceType);
                if (superClass == Object.class) {
                    // Object takes a penalty, so that it is only used if no other type is more specific.
                    //
                    // This is to avoid e.g. Converter<Object, String> from taking priority over
                    // Converter<Option, String> when we're working with a Some<X> where Some implements
                    // interface Option, and has no direct superclass.
                    classDistance = 1_000;
                }
            }
        }

        if (interfaceDistance >= 0 || classDistance >= 0) {
            if (interfaceDistance == -1 || classDistance == -1) {
                return Math.max(interfaceDistance, classDistance) + 1;
            }
            return Math.min(interfaceDistance, classDistance) + 1;
        }
        return -1;
    }

    private GenericConverter getGlobalConverter(final Object source, final Class<?> targetType) {
        final Set<GenericConverter> candidateConverters = new HashSet<>();
        for (final ConditionalConverter converter : this.globalConverters) {
            if (converter.canConvert(source, targetType)) {
                candidateConverters.add((GenericConverter) converter);
            }
        }
        if (candidateConverters.size() == 1) {
            return candidateConverters.iterator().next();
        }
        else if (candidateConverters.size() > 1) {
            DistantConverterMatch<GenericConverter> bestMatch = null;
            for (final GenericConverter candidateConverter : candidateConverters) {
                Set<ConvertibleTypePair> convertibleTypes = candidateConverter.convertibleTypes();
                if (convertibleTypes == null) {
                    if (candidateConverter instanceof final ConverterFactoryAdapter adapter) {
                        convertibleTypes = Set.of(adapter.typePair());
                    }
                    else {
                        continue;
                    }
                }

                final int distance = convertibleTypes.stream()
                        .mapToInt(pair -> this.hierarchyDistance(source.getClass(), pair.sourceType()))
                        .min()
                        .orElse(-1);
                if (distance >= 0) {
                    if (bestMatch == null || distance < bestMatch.distance()) {
                        bestMatch = new DistantConverterMatch<>(candidateConverter, distance);
                    }
                }
            }

            if (bestMatch != null) {
                return bestMatch.target();
            }

            throw new IllegalStateException("Multiple global converters found for source type " + source.getClass() + " and target type " + targetType);
        }
        return null;
    }

    protected GenericConverter getTypeMatchingConverter(final Object source, final Class<?> targetType) {
        final ConvertibleTypePair pair = new ConvertibleTypePair(source.getClass() == null ? null : source.getClass(), targetType);
        return this.getConverterForPair(source, targetType, pair);
    }

    private GenericConverter getConverterForPair(final Object source, final Class<?> targetType, final ConvertibleTypePair pair) {
        for (final GenericConverter converter : this.converters.get(pair)) {
            if (converter instanceof final ConditionalConverter conditionalConverter) {
                if (conditionalConverter.canConvert(source, targetType)) {
                    return converter;
                }
            }
            else {
                return converter;
            }
        }
        return null;
    }

    private record DistantConverterMatch<T>(T target, int distance) {}
}
