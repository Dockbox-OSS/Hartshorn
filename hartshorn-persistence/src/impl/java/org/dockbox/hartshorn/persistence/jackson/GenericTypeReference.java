package org.dockbox.hartshorn.persistence.jackson;

import com.fasterxml.jackson.core.type.TypeReference;

import org.dockbox.hartshorn.persistence.mapping.GenericType;

import java.lang.reflect.Type;

public class GenericTypeReference<T> extends TypeReference<T> {

    private final GenericType<T> genericType;

    public GenericTypeReference(GenericType<T> genericType) {
        this.genericType = genericType;
    }

    @Override
    public Type getType() {
        return this.genericType.getType();
    }
}
