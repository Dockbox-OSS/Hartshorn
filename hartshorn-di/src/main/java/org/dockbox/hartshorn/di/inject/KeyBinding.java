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

package org.dockbox.hartshorn.di.inject;

import org.dockbox.hartshorn.di.annotations.inject.Named;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class KeyBinding<T> {

    private final Class<T> type;
    private final Named annotation;

    public static <T> KeyBinding<T> of(Class<T> type, Named annotation) {
        return new KeyBinding<>(type, annotation);
    }

    public static <T> KeyBinding<T> of(Class<T> type) {
        return new KeyBinding<>(type, null);
    }
}
