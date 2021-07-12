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

package org.dockbox.hartshorn.di.properties;

import org.dockbox.hartshorn.di.binding.Bindings;
import org.dockbox.hartshorn.di.annotations.inject.Named;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class BindingMetaProperty implements InjectorProperty<Named> {

    public static final String KEY = "HartshornInternalBindingMetaProperty";
    @Getter
    private final Named value;

    private BindingMetaProperty(String value) {
        this.value = Bindings.named(value);
    }

    public static BindingMetaProperty of(String value) {
        return new BindingMetaProperty(value);
    }

    public static BindingMetaProperty of(Named meta) {
        return new BindingMetaProperty(meta);
    }

    @Override
    public String key() {
        return KEY;
    }
}
