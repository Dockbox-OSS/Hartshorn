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

package org.dockbox.hartshorn.core.binding;

import org.dockbox.hartshorn.core.Key;

import java.lang.annotation.Annotation;

import javax.inject.Named;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * An implementation of the {@link Named} annotation. This is used by {@link Key}s to allow for {@link String}
 * based names instead of always requiring a {@link Named} instance.
 */
@AllArgsConstructor
public class NamedImpl implements Named {

    @Getter
    private final String value;

    @Override
    public Class<? extends Annotation> annotationType() {
        return Named.class;
    }

    @Override
    public int hashCode() {
        // This is specified in java.lang.Annotation.
        return (127 * "value".hashCode()) ^ this.value.hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof Named other)) return false;
        return this.value.equals(other.value());
    }

    @Override
    public String toString() {
        return "@" + Named.class.getName() + "(value=" + this.value + ")";
    }
}
