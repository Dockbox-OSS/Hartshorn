package org.dockbox.hartshorn.util.introspect.convert;

public interface ConversionService {

    boolean canConvert(Class<?> sourceType, Class<?> targetType);

    <I, O> O convert(I input, Class<O> targetType);
}
