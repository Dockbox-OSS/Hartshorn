package org.dockbox.hartshorn.util.introspect.convert.support;

import java.lang.reflect.Array;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.introspect.convert.DefaultValueProvider;
import org.dockbox.hartshorn.util.introspect.convert.DefaultValueProviderFactory;

public class ArrayDefaultValueProviderFactory implements DefaultValueProviderFactory<Object> {

    @Override
    public <O> DefaultValueProvider<O> create(Class<O> targetType) {
        if (!targetType.isArray()) {
            throw new IllegalArgumentException("Target type must be an array type");
        }
        Class<?> elementType = targetType.getComponentType();
        ArrayDefaultValueProvider<?> provider = new ArrayDefaultValueProvider<>(elementType);
        return TypeUtils.unchecked(provider, DefaultValueProvider.class);
    }

    public static class ArrayDefaultValueProvider<O> implements DefaultValueProvider<O[]> {

        private final Class<O> elementType;

        public ArrayDefaultValueProvider(Class<O> elementType) {
            this.elementType = elementType;
        }

        @Override
        public @Nullable O[] defaultValue() {
            //noinspection unchecked
            return (O[]) Array.newInstance(this.elementType, 0);
        }
    }
}
