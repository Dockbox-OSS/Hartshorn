package org.dockbox.hartshorn.util.introspect.convert;

import java.util.Set;

public interface ConverterCache {

    void addConverter(GenericConverter converter);

    GenericConverter getConverter(Object source, Class<?> targetType);

    Set<GenericConverter> converters();
}
