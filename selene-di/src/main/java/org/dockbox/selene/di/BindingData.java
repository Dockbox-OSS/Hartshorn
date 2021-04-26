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

import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.di.annotations.BindingMeta;

public class BindingData {

    private final Class<?> source;
    private final Class<?> target;
    private final BindingMeta meta;

    public BindingData(Class<?> source, Class<?> target, BindingMeta meta) {
        this.source = source;
        this.target = target;
        this.meta = meta;
    }

    public BindingData(Class<?> source, Class<?> target) {
        this.source = source;
        this.target = target;
        this.meta = null;
    }

    public Class<?> getSource() {
        return this.source;
    }

    public Class<?> getTarget() {
        return this.target;
    }

    public Exceptional<BindingMeta> getMeta() {
        return Exceptional.of(this.meta);
    }
}
