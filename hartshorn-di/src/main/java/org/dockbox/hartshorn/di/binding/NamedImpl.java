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

package org.dockbox.hartshorn.di.binding;

import org.dockbox.hartshorn.di.annotations.inject.Named;

import java.lang.annotation.Annotation;

import lombok.AllArgsConstructor;

@SuppressWarnings("ClassExplicitlyAnnotation")
@AllArgsConstructor
public class NamedImpl implements Named {

    private final String value;

    @Override
    public Class<? extends Annotation> annotationType() {
        return Named.class;
    }

    public int hashCode() {
        // This is specified in java.lang.Annotation.
        return (127 * "value".hashCode()) ^ this.value.hashCode();
    }

    public boolean equals(Object o) {
        if (!(o instanceof Named other)) return false;
        return this.value.equals(other.value());
    }

    public String toString() {
        return "@" + Named.class.getName() + "(value=" + this.value + ")";
    }

    @Override
    public String value() {
        return this.value;
    }
}
