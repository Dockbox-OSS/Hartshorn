/*
 * Copyright 2019-2023 the original author or authors.
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

import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.convert.support.ArrayToCollectionConverterFactory;
import org.dockbox.hartshorn.util.introspect.convert.support.ArrayToObjectConverter;
import org.dockbox.hartshorn.util.introspect.convert.support.CollectionDefaultValueProviderFactory;
import org.dockbox.hartshorn.util.introspect.convert.support.CollectionToArrayConverter;
import org.dockbox.hartshorn.util.introspect.convert.support.CollectionToCollectionConverterFactory;
import org.dockbox.hartshorn.util.introspect.convert.support.CollectionToObjectConverter;
import org.dockbox.hartshorn.util.introspect.convert.support.ObjectToArrayConverter;
import org.dockbox.hartshorn.util.introspect.convert.support.ObjectToCollectionConverterFactory;
import org.dockbox.hartshorn.util.introspect.convert.support.ObjectToOptionConverter;
import org.dockbox.hartshorn.util.introspect.convert.support.ObjectToOptionalConverter;
import org.dockbox.hartshorn.util.introspect.convert.support.ObjectToStringConverter;
import org.dockbox.hartshorn.util.introspect.convert.support.ObjectToVoidConverter;
import org.dockbox.hartshorn.util.introspect.convert.support.OptionToCollectionConverterFactory;
import org.dockbox.hartshorn.util.introspect.convert.support.OptionToObjectConverterFactory;
import org.dockbox.hartshorn.util.introspect.convert.support.OptionToOptionalConverter;
import org.dockbox.hartshorn.util.introspect.convert.support.OptionalToCollectionConverterFactory;
import org.dockbox.hartshorn.util.introspect.convert.support.OptionalToObjectConverter;
import org.dockbox.hartshorn.util.introspect.convert.support.OptionalToOptionConverter;
import org.dockbox.hartshorn.util.introspect.convert.support.PrimitiveWrapperConverter;
import org.dockbox.hartshorn.util.introspect.convert.support.StringToBooleanConverter;
import org.dockbox.hartshorn.util.introspect.convert.support.StringToCharacterConverter;
import org.dockbox.hartshorn.util.introspect.convert.support.StringToEnumConverterFactory;
import org.dockbox.hartshorn.util.introspect.convert.support.StringToNumberConverterFactory;
import org.dockbox.hartshorn.util.introspect.convert.support.StringToPrimitiveConverterFactory;
import org.dockbox.hartshorn.util.introspect.convert.support.StringToUUIDConverter;
import org.dockbox.hartshorn.util.introspect.view.TypeParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Standard implementation of {@link ConversionService} and {@link ConverterRegistry}. The registry implementation
 * is backed by two {@link ConverterCache} instances, one for converters and one for default value providers. The
 * default value providers are stored in a separate cache to avoid clashes with converters that convert from
 * {@link Object} to a specific type. The default value providers are only used when the input is {@code null}.
 *
 * <p>While the {@link StandardConversionService} does not automatically register any converters, it does come
 * with a set of default converters that can be registered using the {@link #withDefaults()} method. These converters
 * include all implementations in the {@link org.dockbox.hartshorn.util.introspect.convert.support} package.
 *
 * <p>The default converters are grouped into several categories, which can be registered individually using the
 * following methods:
 * <ul>
 *     <li>{@link #registerCollectionConverters(ConverterRegistry, ConversionService, Introspector)}</li>
 *     <li>{@link #registerNullWrapperConverters(ConverterRegistry, Introspector)}</li>
 *     <li>{@link #registerStringConverters(ConverterRegistry)}</li>
 *     <li>{@link #registerPrimitiveConverters(ConverterRegistry)}</li>
 *     <li>{@link #registerDefaultProviders(ConverterRegistry, Introspector)}</li>
 * </ul>
 *
 * @author Guus Lieben
 * @since 0.5.0
 */
public class StandardConversionService implements ConversionService, ConverterRegistry {

    private final Introspector introspector;
    private final ConverterCache converterCache;
    // Separate registry for default value providers to avoid clashing with Object.class converters
    private final ConverterCache defaultValueProviderCache;

    public StandardConversionService(final Introspector introspector) {
        this(introspector, new GenericConverters(), new GenericConverters());
    }

    public StandardConversionService(final Introspector introspector, final ConverterCache converterCache, final ConverterCache defaultValueProviderCache) {
        this.introspector = introspector;
        this.converterCache = converterCache;
        this.defaultValueProviderCache = defaultValueProviderCache;
    }

    public StandardConversionService withDefaults() {
        StandardConversionService.registerCollectionConverters(this, this, this.introspector);
        StandardConversionService.registerNullWrapperConverters(this, this.introspector);
        StandardConversionService.registerStringConverters(this);
        StandardConversionService.registerPrimitiveConverters(this);
        StandardConversionService.registerDefaultProviders(this, this.introspector);

        return this;
    }

    @Override
    public boolean canConvert(final Object source, final Class<?> targetType) {
        final Class<?> sourceType = source == null ? null : source.getClass();
        if (sourceType == null || targetType == null) return false;
        if (sourceType.equals(targetType)) return true;
        return this.hasConverterForInput(source, targetType);
    }

    protected boolean hasConverterForInput(final Object source, final Class<?> targetType) {
        return this.converterCache.getConverter(source, targetType) != null;
    }

    @Override
    public <I, O> O convert(final I input, final Class<O> targetType) {
        if (targetType == null) {
            throw new IllegalArgumentException("Target type must not be null");
        }
        if (input == null) {
            return this.convertToDefaultValue(targetType);
        }
        if (targetType.isAssignableFrom(input.getClass())) {
            return targetType.cast(input);
        }
        return this.tryConvert(input, targetType);
    }

    private <I, O> O tryConvert(final I input, final Class<O> targetType) {
        final GenericConverter converter = this.converterCache.getConverter(input, targetType);
        if (converter != null) {
            final TypeView<O> targetTypeView = this.introspector.introspect(targetType);
            final Object converted = converter.convert(input, input.getClass(), targetType);
            if (converted == null) {
                // Ensure we don't return null if the target type is a primitive, or a wrapper for a primitive
                return targetTypeView.defaultOrNull();
            }
            // Use View to cast, as this supports implicit (un)boxing of primitives
            return targetTypeView.cast(converted);
        }

        throw new IllegalArgumentException("No converter found for " + input + " to convert " + input.getClass() + " to " + targetType.getName());
    }

    private <O> O convertToDefaultValue(final Class<O> targetType) {
        final GenericConverter converter = this.defaultValueProviderCache.getConverter(Null.INSTANCE, targetType);
        if (converter != null) {
            final Object defaultValue = converter.convert(null, Null.TYPE, targetType);
            return targetType.cast(defaultValue);
        }
        return this.introspector.introspect(targetType).defaultOrNull();
    }

    @Override
    public <I, O> void addConverter(final Converter<I, O> converter) {
        final List<TypeView<?>> converterParameters = this.introspector.introspect(converter)
                .typeParameters()
                .from(Converter.class);
        final Class<I> sourceType = TypeUtils.adjustWildcards(converterParameters.get(0).type(), Class.class);
        final Class<O> targetType = TypeUtils.adjustWildcards(converterParameters.get(1).type(), Class.class);
        this.addConverter(sourceType, targetType, converter);
    }

    @Override
    public <I, O> void addConverter(final Class<I> sourceType, final Class<O> targetType, final Converter<I, O> converter) {
        final GenericConverter adapter = new ConverterAdapter(sourceType, targetType, converter);
        this.converterCache.addConverter(adapter);
    }

    @Override
    public void addConverter(final GenericConverter converter) {
        this.converterCache.addConverter(converter);
    }

    @Override
    public <I, O> void addConverterFactory(final ConverterFactory<I, O> converterFactory) {
        final Class<I> sourceType = this.getTypeParameter(ConverterFactory.class, converterFactory, 0);
        this.addConverterFactory(sourceType, converterFactory);
    }

    @Override
    public <I, O> void addConverterFactory(final Class<I> sourceType, final ConverterFactory<I, O> converterFactory) {
        final Class<O> targetType = this.getTypeParameter(ConverterFactory.class, converterFactory, 1);
        this.addConverter(new ConverterFactoryAdapter(sourceType, targetType, converterFactory));
    }

    @Override
    public <O> void addDefaultValueProvider(final DefaultValueProvider<O> provider) {
        final Class<O> targetType = this.getTypeParameter(DefaultValueProvider.class, provider, 0);
        this.addDefaultValueProvider(targetType, provider);
    }

    @Override
    public <O> void addDefaultValueProvider(final Class<O> targetType, final DefaultValueProvider<O> provider) {
        final GenericConverter adapter = new ConverterAdapter(Null.TYPE, targetType, provider);
        this.defaultValueProviderCache.addConverter(adapter);
    }

    @Override
    public <O> void addDefaultValueProviderFactory(final DefaultValueProviderFactory<O> factory) {
        final Class<O> targetType = this.getTypeParameter(DefaultValueProviderFactory.class, factory, 0);
        this.defaultValueProviderCache.addConverter(new ConverterFactoryAdapter(Null.TYPE, targetType, factory));
    }

    protected <T, R> Class<R> getTypeParameter(final Class<T> fromType, final T converterFactory, final int parameterIndex) {
        final Class<?> parameter = this.introspector.introspect(converterFactory)
                .typeParameters()
                .resolveInputFor(fromType)
                .atIndex(parameterIndex)
                .flatMap(TypeParameterView::resolvedType)
                .map(TypeView::type)
                .orElseThrow(() -> new IllegalArgumentException("Could not determine type parameter " + parameterIndex + " for " + converterFactory));

        return TypeUtils.adjustWildcards(parameter, Class.class);
    }

    public static void registerCollectionConverters(final ConverterRegistry registry, final ConversionService service, final Introspector introspector) {
        registry.addConverter(new ObjectToArrayConverter());
        registry.addConverter(new ArrayToObjectConverter(service));
        registry.addConverter(new CollectionToArrayConverter());
        registry.addConverter(new CollectionToObjectConverter());

        final ArrayToCollectionConverterFactory arrayToCollectionConverterFactory = new ArrayToCollectionConverterFactory(introspector);
        registry.addConverterFactory(Object[].class, arrayToCollectionConverterFactory);
        registry.addConverterFactory(Object.class, new ObjectToCollectionConverterFactory(arrayToCollectionConverterFactory));
        registry.addConverterFactory(new CollectionToCollectionConverterFactory(introspector));
    }

    public static void registerNullWrapperConverters(final ConverterRegistry registry, final Introspector introspector) {
        registry.addConverter(new ObjectToOptionalConverter());
        registry.addConverter(new ObjectToOptionConverter());
        registry.addConverter(new OptionalToObjectConverter());
        registry.addConverter(new OptionToOptionalConverter());
        registry.addConverter(new OptionalToOptionConverter());

        registry.addConverterFactory(new OptionToObjectConverterFactory());
        registry.addConverterFactory(new OptionToCollectionConverterFactory(introspector));
        registry.addConverterFactory(new OptionalToCollectionConverterFactory(introspector));
    }

    public static void registerStringConverters(final ConverterRegistry registry) {
        registry.addConverter(String.class, Character.class, new StringToCharacterConverter());
        registry.addConverter(String.class, UUID.class,new StringToUUIDConverter());
        registry.addConverter(String.class, Boolean.class, new StringToBooleanConverter());
        registry.addConverter(Object.class, String.class, new ObjectToStringConverter());

        registry.addConverterFactory(String.class, new StringToEnumConverterFactory());
        registry.addConverterFactory(String.class, new StringToNumberConverterFactory());
        registry.addConverterFactory(String.class, new StringToPrimitiveConverterFactory());
    }

    public static void registerPrimitiveConverters(final ConverterRegistry registry) {
        registry.addConverter(new PrimitiveWrapperConverter());
        registry.addConverter(new ObjectToVoidConverter());
    }

    /**
     * Registers a set of default value providers for common types. This does not
     * include value providers for primitives or primitive wrappers, as these are
     * handled by {@link TypeView#defaultOrNull()}.
     *
     * @param registry The registry to register the default value providers to
     * @see TypeView#defaultOrNull()
     */
    public static void registerDefaultProviders(final ConverterRegistry registry, final Introspector introspector) {
        registry.addDefaultValueProvider(Option.class, Option::empty);
        registry.addDefaultValueProvider(String.class, () -> "");
        registry.addDefaultValueProvider(Optional.class, Optional::empty);

        registry.addDefaultValueProviderFactory(new CollectionDefaultValueProviderFactory(introspector).withDefaults());
    }
}
