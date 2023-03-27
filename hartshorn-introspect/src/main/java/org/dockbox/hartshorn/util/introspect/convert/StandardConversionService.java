package org.dockbox.hartshorn.util.introspect.convert;

import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.convert.support.ArrayToCollectionConverterFactory;
import org.dockbox.hartshorn.util.introspect.convert.support.ArrayToObjectConverter;
import org.dockbox.hartshorn.util.introspect.convert.support.ObjectToArrayConverter;
import org.dockbox.hartshorn.util.introspect.convert.support.ObjectToCollectionConverterFactory;
import org.dockbox.hartshorn.util.introspect.convert.support.ObjectToOptionConverter;
import org.dockbox.hartshorn.util.introspect.convert.support.ObjectToOptionalConverter;
import org.dockbox.hartshorn.util.introspect.convert.support.OptionToObjectConverter;
import org.dockbox.hartshorn.util.introspect.convert.support.OptionToOptionalConverter;
import org.dockbox.hartshorn.util.introspect.convert.support.OptionalToObjectConverter;
import org.dockbox.hartshorn.util.introspect.convert.support.StringToBooleanConverter;
import org.dockbox.hartshorn.util.introspect.convert.support.StringToCharacterConverter;
import org.dockbox.hartshorn.util.introspect.convert.support.StringToEnumConverterFactory;
import org.dockbox.hartshorn.util.introspect.convert.support.StringToNumberConverterFactory;
import org.dockbox.hartshorn.util.introspect.convert.support.StringToUUIDConverter;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.util.List;
import java.util.UUID;

public class StandardConversionService implements ConversionService, ConverterRegistry {

    private final Introspector introspector;
    private final GenericConverters genericConverters = new GenericConverters();

    public StandardConversionService(final Introspector introspector) {
        this.introspector = introspector;

        StandardConversionService.registerCollectionConverters(this, this, introspector);
        StandardConversionService.registerNullWrapperConverters(this);
        StandardConversionService.registerStringConverters(this);
    }

    @Override
    public boolean canConvert(final Class<?> sourceType, final Class<?> targetType) {
        if (sourceType == null || targetType == null) return false;
        if (sourceType.equals(targetType)) return true;
        return this.hasConverter(sourceType, targetType);
    }

    @Override
    public <I, O> O convert(final I input, final Class<O> targetType) {
        if (targetType == null) throw new IllegalArgumentException("Target type must not be null");
        if (input == null) {
            // TODO: Default values
            return null;
        }

        if (targetType.isAssignableFrom(input.getClass())) {
            return targetType.cast(input);
        }

        // TODO: Explicit converters
        // ...

        final GenericConverter converter = this.genericConverters.getConverter(input.getClass(), targetType);
        if (converter != null) {
            final Object converted = converter.convert(input, input.getClass(), targetType);
            if (converted == null) return null;
            return targetType.cast(converted);
        }

        throw new IllegalArgumentException("No converter found for " + input.getClass().getName() + " to " + targetType.getName());
    }

    protected boolean hasConverter(final Class<?> sourceType, final Class<?> targetType) {
        final boolean hasDirectConverter = false; // TODO
        if (hasDirectConverter) return true;

        return this.genericConverters.getConverter(sourceType, targetType) != null;
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
        final List<TypeView<?>> factoryParameters = this.introspector.introspect(converterFactory)
                .typeParameters()
                .from(ConverterFactory.class);
        final Class<I> sourceType = TypeUtils.adjustWildcards(factoryParameters.get(0).type(), Class.class);
        this.addConverterFactory(sourceType, converterFactory);
    }

    @Override
    public <I, O> void addConverterFactory(final Class<I> sourceType, final ConverterFactory<I, O> converterFactory) {
        // TODO: Implement!
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
        registry.addConverter(new OptionToObjectConverter());
        registry.addConverter(new OptionToOptionalConverter());
    }

    public static void registerStringConverters(final ConverterRegistry registry) {
        registry.addConverter(String.class, Character.class, new StringToCharacterConverter());
        registry.addConverter(String.class, UUID.class,new StringToUUIDConverter());
        registry.addConverter(String.class, Boolean.class, new StringToBooleanConverter());

        registry.addConverterFactory(String.class, new StringToEnumConverterFactory());
        registry.addConverterFactory(String.class, new StringToNumberConverterFactory());
    }
}
