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
import com.google.inject.Module;
import com.google.inject.Provider;

import org.dockbox.hartshorn.di.annotations.inject.Named;
import org.dockbox.hartshorn.di.inject.KeyBinding;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Supplier;

public class HartshornModule extends AbstractModule {

    private final Map<Key<?>, Provider<?>> providers = HartshornUtils.emptyConcurrentMap();
    private final Map<Key<?>, Class<?>> bindings = HartshornUtils.emptyConcurrentMap();
    private final Set<Module> modules = HartshornUtils.emptyConcurrentSet();

    public <C, T extends C> void add(Class<T> type, Supplier<? extends T> supplier) {
        this.providers.put(Key.get(type), supplier::get);
    }

    public <C, T extends C> void add(Class<C> type, Named named, Supplier<? extends T> supplier) {
        this.providers.put(Key.get(type, named), supplier::get);
    }

    public <C, T extends C> void add(KeyBinding<T> binding, Supplier<? extends T> supplier) {
        if (binding.annotation() != null) this.add(binding.type(), binding.annotation(), supplier);
        else this.add(binding.type(), supplier);
    }

    public <C, T extends C> void add(Class<C> target, Class<? extends T> implementation) {
        this.bindings.put(Key.get(target), implementation);
    }

    public <C, T extends C> void add(Class<C> target, Named named, Class<? extends T> implementation) {
        this.bindings.put(Key.get(target, named), implementation);
    }

    public void add(Map<Key<?>, Class<?>> bindings) {
        this.bindings.putAll(bindings);
    }

    public void add(Module module) {
        this.modules.add(module);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void configure() {
        for (Module module : this.modules) {
            this.install(module);
        }
        for (Entry<Key<?>, Provider<?>> entry : this.providers.entrySet()) {
            this.bind((Key<Object>) entry.getKey()).toProvider(entry.getValue());
        }
        for (Entry<Key<?>, Class<?>> entry : this.bindings.entrySet()) {
            this.bind((Key<Object>) entry.getKey()).to(entry.getValue());
        }
    }
}
