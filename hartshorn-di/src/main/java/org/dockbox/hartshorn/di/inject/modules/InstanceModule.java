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
import com.google.inject.Key;

public class InstanceModule<T> extends AbstractModule {

    private final Key<T> target;
    private final T instance;

    public InstanceModule(Class<T> target, T instance) {
        this.target = Key.get(target);
        this.instance = instance;
    }

    @SuppressWarnings("unchecked")
    public InstanceModule(Key<?> key, T instance) {
        this.target = (Key<T>) key;
        this.instance = instance;
    }

    @Override
    protected void configure() {
        super.configure();
        this.bind(this.target).toInstance(this.instance);
    }
}
