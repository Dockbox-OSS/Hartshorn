package org.dockbox.hartshorn.util.introspect.convert;

public interface ConverterRegistry {

    <I, O> void addConverter(Converter<I, O>  converter);

    <I, O> void addConverter(Class<I> sourceType, Class<O> targetType, Converter<I, O> converter);

    void addConverter(GenericConverter converter);

    <I, O> void addConverterFactory(ConverterFactory<I, O>  converterFactory);

    <I, O> void addConverterFactory(Class<I> sourceType, ConverterFactory<I, O> converterFactory);

    <O> void addDefaultValueProvider(DefaultValueProvider<O> provider);

    <O> void addDefaultValueProvider(Class<O> targetType, DefaultValueProvider<O> provider);

    <O> void addDefaultValueProviderFactory(DefaultValueProviderFactory<O> factory);

}
