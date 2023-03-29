package org.dockbox.hartshorn.util.introspect.convert;

public interface ConversionService {

    boolean canConvert(Object source, Class<?> targetType);

    <I, O> O convert(I input, Class<O> targetType);
}
