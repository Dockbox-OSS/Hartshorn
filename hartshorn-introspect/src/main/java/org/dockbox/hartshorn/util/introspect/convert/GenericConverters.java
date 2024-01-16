/*
 * Copyright 2019-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dockbox.hartshorn.util.introspect.convert;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.util.CollectionUtilities;
import org.dockbox.hartshorn.util.collections.ConcurrentSetMultiMap;
import org.dockbox.hartshorn.util.collections.MultiMap;

/**
 * A {@link ConverterCache} for {@link GenericConverter}s. If a converter implements {@link ConditionalConverter},
 * it will be used to narrow the source/target type. Otherwise, the {@link ConvertibleTypePair} will be used to
 * determine whether a converter can be used.
 *
 * <p>It is possible for multiple {@link GenericConverter}s exist for a single {@link ConvertibleTypePair}, which
 * may be required for factory-based converters. In this case, the most specific converter will be used. In all
 * cases, if multiple converters are found, it is expected that they implement {@link ConditionalConverter} to
 * narrow the match.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class GenericConverters implements ConverterCache {

    private final Set<ConditionalConverter> globalConverters = ConcurrentHashMap.newKeySet();
    private final MultiMap<ConvertibleTypePair, GenericConverter> converters = new ConcurrentSetMultiMap<>();

    @Override
    public void addConverter(GenericConverter converter) {
        Set<ConvertibleTypePair> convertibleTypes = converter.convertibleTypes();
        if (convertibleTypes == null) {
            if (converter instanceof ConditionalConverter conditionalConverter) {
                this.globalConverters.add(conditionalConverter);
            }
            else {
                throw new IllegalArgumentException("Converter must implement ConditionalConverter if convertibleTypes() returns null");
            }
        }
        else {
            for (ConvertibleTypePair convertibleType : convertibleTypes) {
                this.converters.put(convertibleType, converter);
            }
        }
    }

    @Nullable
    @Override
    public GenericConverter getConverter(Object source, Class<?> targetType) {
        GenericConverter converter = this.getTypeMatchingConverter(source, targetType);
        if (converter == null) {
            converter = this.getGlobalConverter(source, targetType);
        }
        if (converter == null) {
            converter = this.getClosestMatchingConverter(source, targetType);
        }
        return converter;
    }

    @Override
    public Set<GenericConverter> converters() {
        Set<GenericConverter> converters = new HashSet<>(this.converters.allValues());
        this.globalConverters.stream()
                .map(converter -> (GenericConverter) converter)
                .forEach(converters::add);
        return converters;
    }

    @Nullable
    private GenericConverter getClosestMatchingConverter(Object source, Class<?> targetType) {
        Set<GenericConverter> matchingConverters = new HashSet<>();
        for (ConvertibleTypePair typePair : this.converters.keySet()) {
            Class<?> inputType = source.getClass();
            Class<?> sourceType = typePair.sourceType();
            if (inputType.isArray() && sourceType.isArray()) {
                inputType = inputType.getComponentType();
                sourceType = sourceType.getComponentType();
            }

            // Recursive solution to iterate super classes first, then interfaces
            if (sourceType.isAssignableFrom(inputType) && typePair.targetType().isAssignableFrom(targetType)) {
                // distance is the amount of classes in the hierarchy between the typePair sourceType and the source class
                // the closer the distance, the more specific the typePair sourceType is
                // the more specific the typePair sourceType is, the more likely it is that the converter can convert the source
                // to the target type
                int distance = this.hierarchyDistance(inputType, sourceType);
                if (distance >= 0) {
                    matchingConverters.addAll(this.converters.get(typePair));
                }
            }
        }
        return this.findMatchingConverter(source, targetType, matchingConverters);
    }

    private int hierarchyDistance(Class<?> inputType, Class<?> sourceType) {
        if (inputType == sourceType) {
            return 0;
        }

        int interfaceDistance = -1;
        for (Class<?> interfaceType : inputType.getInterfaces()) {
            if (sourceType.isAssignableFrom(interfaceType)) {
                int distance = this.hierarchyDistance(interfaceType, sourceType);
                if (distance >= 0) {
                    interfaceDistance = distance;
                }
            }
        }

        int classDistance = -1;
        Class<?> superClass = inputType.getSuperclass();
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

    @Nullable
    private GenericConverter getGlobalConverter(Object source, Class<?> targetType) {
        Set<GenericConverter> candidateConverters = new HashSet<>();
        for (ConditionalConverter converter : this.globalConverters) {
            if (converter.canConvert(source, targetType)) {
                candidateConverters.add((GenericConverter) converter);
            }
        }
        if (candidateConverters.size() == 1) {
            return CollectionUtilities.first(candidateConverters);
        }
        else if (candidateConverters.size() > 1) {
            return this.findMatchingConverter(source, targetType, candidateConverters);
        }
        return null;
    }

    @Nullable
    private GenericConverter findMatchingConverter(Object source, Class<?> targetType, Set<GenericConverter> candidateConverters) {
        if (candidateConverters.isEmpty()) {
            return null;
        }

        int bestDistance = -1;
        GenericConverter bestConverter = null;

        for (GenericConverter candidateConverter : candidateConverters) {
            if (candidateConverter instanceof ConditionalConverter conditionalConverter) {
                if (!conditionalConverter.canConvert(source, targetType)) {
                    continue;
                }
            }

            Set<ConvertibleTypePair> convertibleTypes = candidateConverter.convertibleTypes();
            if (convertibleTypes == null) {
                if (candidateConverter instanceof ConverterFactoryAdapter adapter) {
                    convertibleTypes = Set.of(adapter.typePair());
                }
                else {
                    continue;
                }
            }

            int distance = convertibleTypes.stream()
                    .mapToInt(pair -> this.hierarchyDistance(source.getClass(), pair.sourceType()))
                    .min()
                    .orElse(-1);

            if (distance >= 0) {
                if (bestConverter != null && distance == bestDistance) {
                    throw new AmbiguousConverterException("Ambiguous converters found for source type [" + source.getClass().getName() + "] and target type [" + targetType.getName() + "]: " + bestConverter + ", " + candidateConverter);
                }

                if (bestConverter == null || distance < bestDistance) {
                    bestDistance = distance;
                    bestConverter = candidateConverter;
                }
            }
        }

        return bestConverter;
    }

    @Nullable
    protected GenericConverter getTypeMatchingConverter(Object source, Class<?> targetType) {
        ConvertibleTypePair pair = new ConvertibleTypePair(source == null ? null : source.getClass(), targetType);
        return this.getConverterForPair(source, targetType, pair);
    }

    @Nullable
    private GenericConverter getConverterForPair(Object source, Class<?> targetType, ConvertibleTypePair pair) {
        List<GenericConverter> matchingConverters = new ArrayList<>();
        for (GenericConverter converter : this.converters.get(pair)) {
            if (converter instanceof ConditionalConverter conditionalConverter) {
                if (conditionalConverter.canConvert(source, targetType)) {
                    matchingConverters.add(converter);
                }
            }
            else {
                matchingConverters.add(converter);
            }
        }
        if (matchingConverters.size() == 1) {
            return matchingConverters.get(0);
        }
        else if (matchingConverters.size() > 1) {
            throw new AmbiguousConverterException("Ambiguous converters found for source type [" + source.getClass().getName() + "] and target type [" + targetType.getName() + "]: " + matchingConverters);
        }
        return null;
    }
}
