package org.dockbox.hartshorn.util.introspect.convert;

public interface DefaultValueProviderFactory<T> extends ConverterFactory<Null, T> {

    @Override
    <O extends T> DefaultValueProvider<O> create(Class<O> targetType);
}
