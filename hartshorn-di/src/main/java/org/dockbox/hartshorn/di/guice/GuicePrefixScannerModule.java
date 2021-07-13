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

package org.dockbox.hartshorn.di.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Key;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.util.Reflect;

import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Singleton;

public class GuicePrefixScannerModule extends AbstractModule {

    private final Map<Key<?>, Class<?>> bindings;

    public GuicePrefixScannerModule(Map<Key<?>, Class<?>> bindings) {
        this.bindings = bindings;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void configure() {
        for (Entry<Key<?>, Class<?>> entry : this.bindings.entrySet()) {
            final Exceptional<Singleton> annotation = Reflect.annotation(entry.getKey().getTypeLiteral().getRawType(), Singleton.class);
            if (annotation.present()) {
                this.bind((Key<Object>) entry.getKey()).to(entry.getValue()).asEagerSingleton();
            } else {
                this.bind((Key<Object>) entry.getKey()).to(entry.getValue());
            }
        }
    }
}
