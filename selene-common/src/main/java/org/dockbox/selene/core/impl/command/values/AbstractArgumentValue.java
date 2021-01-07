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

package org.dockbox.selene.core.impl.command.values;

import org.dockbox.selene.core.impl.command.convert.ArgumentConverter;
import org.dockbox.selene.core.impl.command.convert.impl.ArgumentConverterRegistry;
import org.dockbox.selene.core.objects.Exceptional;

public abstract class AbstractArgumentValue<T> {

    private final String permission;
    private T value;

    protected AbstractArgumentValue(String permission, String key, String type) {
        Exceptional<ArgumentConverter<?>> converter = ArgumentConverterRegistry.getOptionalConverter(type.toLowerCase());
        if (converter.isPresent())
            this.value = this.parseValue(converter.get(), key, type);

        this.permission = permission;
    }

    protected abstract T parseValue(ArgumentConverter<?> converter, String key, String type);

    public String getPermission() {
        return this.permission;
    }

    public abstract AbstractArgumentElement<T> getElement();

    public T getValue() {
        return this.value;
    }

    public void setValue(T value) {
        this.value = value;
    }

}
