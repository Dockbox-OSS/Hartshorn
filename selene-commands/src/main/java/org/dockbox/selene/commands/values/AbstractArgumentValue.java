/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.commands.values;

import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.api.exceptions.Except;
import org.dockbox.selene.commands.context.ArgumentConverter;
import org.dockbox.selene.commands.convert.ArgumentConverterRegistry;

public abstract class AbstractArgumentValue<T> implements ArgumentValue<T> {

    private final String permission;
    private T value;

    protected AbstractArgumentValue(String permission, String key, String type) {
        Exceptional<ArgumentConverter<?>> converter = ArgumentConverterRegistry.getOptionalConverter(type.toLowerCase());
        if (converter.present()) this.value = this.parseValue(converter.get(), key, type);
        this.permission = permission;
        this.prepareValue(type, key);
    }

    @SuppressWarnings("unchecked")
    private <E extends Enum<E>> void prepareValue(String type, String key) {
        if (!ArgumentConverterRegistry.hasConverter(type.toLowerCase())) {
            try {
                Class<?> clazz = Class.forName(type);
                if (clazz.isEnum()) {
                    Class<E> enumType = (Class<E>) clazz;
                    this.setEnumType(enumType, key);
                }
                else //noinspection ThrowCaughtLocally
                    throw new IllegalArgumentException("Type '" + type.toLowerCase() + "' is not supported");
            }
            catch (Exception e) {
                Except.handle("No argument of type `" + type + "` can be read", e);
            }
        }
    }

    protected abstract T parseValue(ArgumentConverter<?> converter, String key, String type);

    protected abstract <E extends Enum<E>> void setEnumType(Class<E> enumType, String key);

    @Override
    public String getPermission() {
        return this.permission;
    }

    @Override
    public abstract AbstractArgumentElement<T> getElement();

    @Override
    public T getValue() {
        return this.value;
    }

    @Override
    public void setValue(T value) {
        this.value = value;
    }
}
