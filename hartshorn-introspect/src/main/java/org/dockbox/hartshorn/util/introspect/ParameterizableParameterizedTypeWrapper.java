package org.dockbox.hartshorn.util.introspect;

import java.lang.reflect.ParameterizedType;

record ParameterizableParameterizedTypeWrapper<T>(ParameterizableType<T> type) implements ParameterizedType {

    @Override
    public java.lang.reflect.Type[] getActualTypeArguments() {
        return this.type.parameters().stream()
                .map(ParameterizableType::asParameterizedType)
                .toArray(java.lang.reflect.Type[]::new);
    }

    @Override
    public java.lang.reflect.Type getRawType() {
        return this.type.type();
    }

    @Override
    public java.lang.reflect.Type getOwnerType() {
        return null;
    }
}
