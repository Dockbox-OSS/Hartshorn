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

package org.dockbox.selene.di.properties;

import org.dockbox.selene.di.binding.Bindings;
import org.dockbox.selene.di.annotations.BindingMeta;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class BindingMetaProperty implements InjectorProperty<BindingMeta> {

    public static final String KEY = "SeleneInternalBindingMetaProperty";
    @Getter
    private final BindingMeta object;

    private BindingMetaProperty(String value) {
        this.object = Bindings.meta(value);
    }

    public static BindingMetaProperty of(String value) {
        return new BindingMetaProperty(value);
    }

    public static BindingMetaProperty of(BindingMeta meta) {
        return new BindingMetaProperty(meta);
    }

    @Override
    public String getKey() {
        return KEY;
    }
}
