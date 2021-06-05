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

package org.dockbox.hartshorn.di.inject.modules;

import com.google.inject.AbstractModule;

import org.dockbox.hartshorn.di.annotations.Named;

public class InstanceMetaModule<T> extends AbstractModule {

    private final Class<T> target;
    private final T instance;
    private final Named meta;

    public InstanceMetaModule(Class<T> target, T instance,  Named meta) {
        this.target = target;
        this.instance = instance;
        this.meta = meta;
    }

    @Override
    protected void configure() {
        super.configure();
        this.bind(this.target).annotatedWith(this.meta).toInstance(this.instance);
    }
}
