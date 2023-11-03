package org.dockbox.hartshorn.util.introspect;

import java.lang.reflect.ParameterizedType;
import java.util.Objects;

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

    @Override
    public boolean equals(Object object) {
        if(this == object) {
            return true;
        }
        if(!(object instanceof ParameterizableParameterizedTypeWrapper<?> that)) {
            return false;
        }
        return Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }
}
