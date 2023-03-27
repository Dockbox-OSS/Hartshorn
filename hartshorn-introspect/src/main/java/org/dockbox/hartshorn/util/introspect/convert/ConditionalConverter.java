package org.dockbox.hartshorn.util.introspect.convert;

public interface ConditionalConverter {

    boolean canConvert(Object source, Class<?> targetType);
}
