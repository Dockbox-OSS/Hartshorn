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

package org.dockbox.selene.di;

import org.dockbox.selene.di.annotations.BindingMeta;

import java.lang.annotation.Annotation;

@SuppressWarnings("ClassExplicitlyAnnotation")
class BindingMetaImpl implements BindingMeta {

    private final String value;

    public BindingMetaImpl(String value) {
        this.value = value;
    }

    @Override
    public String value() {
        return this.value;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return BindingMeta.class;
    }

    public int hashCode() {
        // This is specified in java.lang.Annotation.
        return (127 * "value".hashCode()) ^ this.value.hashCode();
    }

    public boolean equals(Object o) {
        if (!(o instanceof BindingMeta)) {
            return false;
        }

        BindingMeta other = (BindingMeta) o;
        return this.value.equals(other.value());
    }

    public String toString() {
        return "@" + BindingMeta.class.getName() + "(value=" + this.value + ")";
    }
}
