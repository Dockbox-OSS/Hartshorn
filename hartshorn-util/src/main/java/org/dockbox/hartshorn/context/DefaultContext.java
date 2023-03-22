/*
 * Copyright 2019-2023 the original author or authors.
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

import org.dockbox.hartshorn.util.StringUtilities;
import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.collections.SynchronizedMultiMap.SynchronizedHashSetMultiMap;
import org.dockbox.hartshorn.util.option.Option;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * The default implementation of {@link Context}. This implementation uses a {@link SynchronizedHashSetMultiMap} to store the
 * contexts.
 */
public abstract class DefaultContext implements Context {

    private transient Set<Context> unnamedContexts;
    private transient MultiMap<String, Context> namedContexts;

    protected Set<Context> unnamedContexts() {
        if (this.unnamedContexts == null) {
            this.unnamedContexts = ConcurrentHashMap.newKeySet();
        }
        return this.unnamedContexts;
    }

    protected MultiMap<String, Context> namedContexts() {
        if (this.namedContexts == null) {
            this.namedContexts = new SynchronizedHashSetMultiMap<>();
        }
        return this.namedContexts;
    }

    @Override
    public <C extends Context> void add(final C context) {
        if (context instanceof NamedContext named && StringUtilities.notEmpty(named.name())) {
            this.namedContexts().put(named.name(), context);
        }
        else if (context != null) {
            this.unnamedContexts().add(context);
        }
    }

    @Override
    public <C extends Context> void add(final String name, final C context) {
        if (context instanceof NamedContext named && !named.name().equals(name)) {
            throw new IllegalArgumentException(("Context name does not match the provided name. " +
                    "Context name: %s, provided name: %s. Either use only the name of the context, " +
                    "or encapsulate the context so the appropriate name is used."
            ).formatted(named.name(), name));
        }
        else if (context != null) {
            this.namedContexts().put(name, context);
        }
    }

    @Override
    public List<Context> all() {
        final List<Context> contexts = new ArrayList<>();
        if (this.unnamedContexts != null) contexts.addAll(this.unnamedContexts);
        if (this.namedContexts != null) contexts.addAll(this.namedContexts.allValues());
        return Collections.unmodifiableList(contexts);
    }

    @Override
    public <C extends Context> Option<C> first(final ContextIdentity<C> key) {
        return Option.of(this.stream(key).findFirst())
                .orCompute(() -> {
                    if (key.requiresApplicationContext()) return null;
                    final C context = key.create();
                    this.add(context);
                    return context;
                });
    }

    @Override
    public <C extends Context> List<C> all(final ContextIdentity<C> key) {
        return this.stream(key).toList();
    }

    protected <C extends Context> Stream<C> stream(final ContextIdentity<C> key) {
        final Stream<Context> contexts = StringUtilities.empty(key.name())
                ? this.unnamedContexts().stream()
                : this.namedContexts().get(key.name()).stream();

        return contexts.filter(key.type()::isInstance)
                .map(key.type()::cast);
    }

    @Override
    public <C extends Context> Option<C> first(final Class<C> context) {
        return this.first(new SimpleContextIdentity<>(context));
    }

    @Override
    public <C extends Context> List<C> all(final Class<C> context) {
        return this.all(new SimpleContextIdentity<>(context));
    }
}
