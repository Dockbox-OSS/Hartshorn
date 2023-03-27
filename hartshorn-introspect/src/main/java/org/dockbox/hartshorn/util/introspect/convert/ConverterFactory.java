package org.dockbox.hartshorn.util.introspect.convert;

public interface ConverterFactory<I, R> {

    <O extends R> Converter<I, O> create(Class<O> targetType);
}
