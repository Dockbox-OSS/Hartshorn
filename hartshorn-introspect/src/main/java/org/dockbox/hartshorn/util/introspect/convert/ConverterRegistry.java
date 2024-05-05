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

/**
 * A registry for converters. Converters can be registered with this registry to customize the conversion
 * process, typically performed by a {@link ConversionService} implementation.
 *
 * <p>This contract does not expose the ability to lookup converters or to remove converters from the registry.
 * This is intentional as the registry is intended to be used by a {@link ConversionService} implementation
 * to register converters and not by clients of the {@link ConversionService} to lookup converters.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public interface ConverterRegistry {

    /**
     * Add a converter to the registry. The source and target types of the converter are determined
     * by the generic type parameters of the converter.
     *
     * @param converter the converter to add
     * @param <I> the source type
     * @param <O> the target type
     */
    <I, O> void addConverter(Converter<I, O>  converter);

    /**
     * Add a converter to the registry. The source and target types are to be used to determine
     * whether the converter can be used to convert a given source object to a given target type in
     * a {@link ConversionService}.
     *
     * @param sourceType the source type
     * @param targetType the target type
     * @param converter the converter to add
     * @param <I> the source type
     * @param <O> the target type
     */
    <I, O> void addConverter(Class<I> sourceType, Class<O> targetType, Converter<I, O> converter);

    /**
     * Add a generic converter to the registry. The source and target types are determined from
     * the {@link GenericConverter#convertibleTypes()} method.
     *
     * @param converter the converter to add
     */
    void addConverter(GenericConverter converter);

    /**
     * Add a converter factory to the registry. The source and target types of the converter are determined
     * by the generic type parameters of the converter factory.
     *
     * @param converterFactory the converter factory to add
     * @param <I> the source type
     * @param <O> the target type
     */
    <I, O> void addConverterFactory(ConverterFactory<I, O>  converterFactory);

    /**
     * Add a converter factory to the registry. The source type is to be used to determine whether the
     * converter factory can be used to create a converter to convert a given source object to a given
     * target type in a {@link ConversionService}.
     *
     * @param sourceType the source type
     * @param converterFactory the converter factory to add
     * @param <I> the source type
     * @param <O> the target type
     */
    <I, O> void addConverterFactory(Class<I> sourceType, ConverterFactory<I, O> converterFactory);

    /**
     * Add a default value provider to the registry. The target type of the provider is determined
     * by the generic type parameter of the provider.
     *
     * @param provider the provider to add
     * @param <O> the target type
     */
    <O> void addDefaultValueProvider(DefaultValueProvider<O> provider);

    /**
     * Add a default value provider to the registry. The target type is to be used to determine whether the
     * provider can be used to provide a default value for a given target type in a {@link ConversionService}.
     *
     * @param targetType the target type
     * @param provider the provider to add
     * @param <O> the target type
     */
    <O> void addDefaultValueProvider(Class<O> targetType, DefaultValueProvider<O> provider);

    /**
     * Add a default value provider factory to the registry. The target type of the provider is determined
     * by the generic type parameter of the provider factory.
     *
     * @param factory the factory to add
     * @param <O> the target type
     */
    <O> void addDefaultValueProviderFactory(DefaultValueProviderFactory<O> factory);

}
