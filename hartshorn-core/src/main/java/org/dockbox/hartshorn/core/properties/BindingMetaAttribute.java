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

package org.dockbox.hartshorn.core.properties;

import org.dockbox.hartshorn.core.binding.Bindings;

import javax.inject.Named;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class BindingMetaAttribute implements Attribute<Named> {

    @Getter private final Named value;

    private BindingMetaAttribute(final String value) {
        this.value = Bindings.named(value);
    }

    public static BindingMetaAttribute of(final String value) {
        return new BindingMetaAttribute(value);
    }

    public static BindingMetaAttribute of(final Named meta) {
        return new BindingMetaAttribute(meta);
    }
}
