/*
 * Copyright 2019-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dockbox.hartshorn.context;

import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.util.StringUtilities;
import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.collections.StandardMultiMap.ConcurrentSetMultiMap;
import org.dockbox.hartshorn.util.collections.SynchronizedMultiMap.SynchronizedHashSetMultiMap;
import org.dockbox.hartshorn.util.option.Option;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The default implementation of {@link Context}. This implementation uses a {@link SynchronizedHashSetMultiMap} to store the
 * contexts.
 */
public abstract class DefaultContext implements Context {

    protected final transient Set<Context> contexts = ConcurrentHashMap.newKeySet();
    protected final transient MultiMap<String, Context> namedContexts = new ConcurrentSetMultiMap<>();

    @Override
    public <C extends Context> void add(final C context) {
        if (context instanceof NamedContext named) this.add(named);
        if (context != null) this.contexts.add(context);
    }

    @Override
    public <N extends NamedContext> void add(final N context) {
        if (context != null && StringUtilities.notEmpty(context.name()))
            this.namedContexts.put(context.name(), context);
    }

    @Override
    public <C extends Context> void add(final String name, final C context) {
        if (context != null && StringUtilities.notEmpty(name))
            this.namedContexts.put(name, context);
    }

    @Override
    public Option<Context> first(final String name) {
        return Option.of(this.namedContexts.get(name).stream().findFirst());
    }

    @Override
    public <N extends Context> Option<N> first(final String name, final Class<N> context) {
        return Option.of(this.namedContexts.get(name).stream()
                        .filter(c -> context.isAssignableFrom(c.getClass()))
                        .findFirst())
                .map(context::cast);
    }

    @Override
    public List<Context> all() {
        return List.copyOf(this.contexts);
    }

    @Override
    public <C extends Context> List<C> all(final Class<C> context) {
        return this.contexts.stream()
                .filter(c -> c.getClass().equals(context))
                .map(context::cast)
                .toList();
    }

    @Override
    public List<Context> all(final String name) {
        return List.copyOf(this.namedContexts.get(name));
    }

    @Override
    public <N extends Context> List<N> all(final String name, final Class<N> context) {
        return this.namedContexts.get(name).stream()
                .filter(c -> context.isAssignableFrom(c.getClass()))
                .map(context::cast)
                .toList();
    }


    @Override
    public <C extends Context> Option<C> first(final Class<C> context) {
        return Option.of(this.contexts.stream()
                .filter(c -> context.isAssignableFrom(c.getClass()))
                .map(context::cast)
                .findFirst());
    }

    @Override
    public <C extends Context> Option<C> first(final Class<C> context, final String name) {
        return Option.of(this.namedContexts.get(name).stream()
                .filter(c -> context.isAssignableFrom(c.getClass()))
                .map(context::cast)
                .findFirst());
    }

    @Override
    public <C extends Context> Option<C> first(final ComponentKey<C> key) {
        if (key.name() == null) return this.first(key.type());
        else return this.first(key.type(), key.name());
    }
}
