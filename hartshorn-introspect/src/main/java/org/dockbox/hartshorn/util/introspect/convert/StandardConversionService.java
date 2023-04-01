package org.dockbox.hartshorn.util.introspect.convert;

import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.convert.support.ArrayToCollectionConverterFactory;
import org.dockbox.hartshorn.util.introspect.convert.support.ArrayToObjectConverter;
import org.dockbox.hartshorn.util.introspect.convert.support.CollectionDefaultValueProviderFactory;
import org.dockbox.hartshorn.util.introspect.convert.support.ObjectToArrayConverter;
import org.dockbox.hartshorn.util.introspect.convert.support.ObjectToCollectionConverterFactory;
import org.dockbox.hartshorn.util.introspect.convert.support.ObjectToOptionConverter;
import org.dockbox.hartshorn.util.introspect.convert.support.ObjectToOptionalConverter;
import org.dockbox.hartshorn.util.introspect.convert.support.ObjectToStringConverter;
import org.dockbox.hartshorn.util.introspect.convert.support.OptionToObjectConverterFactory;
import org.dockbox.hartshorn.util.introspect.convert.support.OptionToOptionalConverter;
import org.dockbox.hartshorn.util.introspect.convert.support.OptionalToObjectConverter;
import org.dockbox.hartshorn.util.introspect.convert.support.StringToBooleanConverter;
import org.dockbox.hartshorn.util.introspect.convert.support.StringToCharacterConverter;
import org.dockbox.hartshorn.util.introspect.convert.support.StringToEnumConverterFactory;
import org.dockbox.hartshorn.util.introspect.convert.support.StringToNumberConverterFactory;
import org.dockbox.hartshorn.util.introspect.convert.support.StringToPrimitiveConverterFactory;
import org.dockbox.hartshorn.util.introspect.convert.support.StringToUUIDConverter;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class StandardConversionService implements ConversionService, ConverterRegistry {

    private final Introspector introspector;
    private final GenericConverters genericConverters = new GenericConverters();
    // Separate registry for default value providers to avoid clashing with Object.class converters
    private final GenericConverters defaultValueProviders = new GenericConverters();

    public StandardConversionService(final Introspector introspector) {
        this.introspector = introspector;

        StandardConversionService.registerCollectionConverters(this, this, introspector);
        StandardConversionService.registerNullWrapperConverters(this);
        StandardConversionService.registerStringConverters(this);
        StandardConversionService.registerDefaultProviders(this, introspector);
    }

    @Override
    public boolean canConvert(final Object source, final Class<?> targetType) {
        final Class<?> sourceType = source == null ? null : source.getClass();
        if (sourceType == null || targetType == null) return false;
        if (sourceType.equals(targetType)) return true;
        return this.hasConverterForInput(source, targetType);
    }

    protected boolean hasConverterForInput(final Object source, final Class<?> targetType) {
        return this.genericConverters.getConverter(source, targetType) != null;
    }

    @Override
    public <I, O> O convert(final I input, final Class<O> targetType) {
        if (targetType == null) throw new IllegalArgumentException("Target type must not be null");
        if (input == null) {
            final GenericConverter converter = this.defaultValueProviders.getConverter(Null.INSTANCE, targetType);
            if (converter != null) {
                final Object defaultValue = converter.convert(null, Null.TYPE, targetType);
                return targetType.cast(defaultValue);
            }
            return this.introspector.introspect(targetType).defaultOrNull();
        }

        if (targetType.isAssignableFrom(input.getClass())) {
            return targetType.cast(input);
        }

        final GenericConverter converter = this.genericConverters.getConverter(input, targetType);
        if (converter != null) {
            final TypeView<O> targetTypeView = this.introspector.introspect(targetType);
            final Object converted = converter.convert(input, input.getClass(), targetType);
            if (converted == null) {
                return targetTypeView.defaultOrNull();
            }
            // Use View to cast, as this supports implicit (un)boxing of primitives
            return targetTypeView.cast(converted);
        }

        throw new IllegalArgumentException("No converter found for " + input + " to convert " + input.getClass() + " to " + targetType.getName());
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
        this.genericConverters.addConverter(adapter);
    }

    @Override
    public void addConverter(final GenericConverter converter) {
        this.genericConverters.addConverter(converter);
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
    public <O> void addDefaultValueProvider(DefaultValueProvider<O> provider) {
        Class<O> targetType = this.getTypeParameter(DefaultValueProvider.class, provider, 0);
        this.addDefaultValueProvider(targetType, provider);
    }

    @Override
    public <O> void addDefaultValueProvider(Class<O> targetType, DefaultValueProvider<O> provider) {
        this.addConverter(Null.TYPE, targetType, provider);
    }

    @Override
    public <O> void addDefaultValueProviderFactory(DefaultValueProviderFactory<O> factory) {
        Class<O> targetType = this.getTypeParameter(DefaultValueProviderFactory.class, factory, 0);
        this.addConverter(new ConverterFactoryAdapter(Null.TYPE, targetType, factory));
    }

    protected <T, R> Class<R> getTypeParameter(final Class<T> fromType, final T converterFactory, final int parameterIndex) {
        final List<TypeView<?>> parameters = this.introspector.introspect(converterFactory)
                .typeParameters()
                .from(fromType);
        return TypeUtils.adjustWildcards(parameters.get(parameterIndex).type(), Class.class);
    }

    public static void registerCollectionConverters(final ConverterRegistry registry, final ConversionService service, final Introspector introspector) {
        registry.addConverter(new ObjectToArrayConverter());
        registry.addConverter(new ArrayToObjectConverter(service));

        final ArrayToCollectionConverterFactory arrayToCollectionConverterFactory = new ArrayToCollectionConverterFactory(introspector);
        registry.addConverterFactory(Object[].class, arrayToCollectionConverterFactory);
        registry.addConverterFactory(Object.class, new ObjectToCollectionConverterFactory(arrayToCollectionConverterFactory));
    }

    public static void registerNullWrapperConverters(final ConverterRegistry registry) {
        registry.addConverter(new ObjectToOptionalConverter());
        registry.addConverter(new ObjectToOptionConverter());
        registry.addConverter(new OptionalToObjectConverter());
        registry.addConverterFactory(new OptionToObjectConverterFactory());
        registry.addConverter(new OptionToOptionalConverter());
    }

    public static void registerStringConverters(final ConverterRegistry registry) {
        registry.addConverter(String.class, Character.class, new StringToCharacterConverter());
        registry.addConverter(String.class, UUID.class,new StringToUUIDConverter());
        registry.addConverter(String.class, Boolean.class, new StringToBooleanConverter());

        registry.addConverterFactory(String.class, new StringToEnumConverterFactory());
        registry.addConverterFactory(String.class, new StringToNumberConverterFactory());
        registry.addConverterFactory(String.class, new StringToPrimitiveConverterFactory());

        registry.addConverter(Object.class, String.class, new ObjectToStringConverter());
    }

    /**
     * Registers a set of default value providers for common types. This does not
     * include value providers for primitives or primitive wrappers, as these are
     * handled by {@link TypeView#defaultOrNull()}.
     *
     * @param registry The registry to register the default value providers to
     * @see TypeView#defaultOrNull()
     */
    private static void registerDefaultProviders(final ConverterRegistry registry, final Introspector introspector) {
        registry.addDefaultValueProvider(Option.class, Option::empty);
        registry.addDefaultValueProvider(String.class, () -> "");
        registry.addDefaultValueProvider(Optional.class, Optional::empty);
        registry.addDefaultValueProviderFactory(new CollectionDefaultValueProviderFactory(introspector));
    }
}
