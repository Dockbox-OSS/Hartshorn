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

package org.dockbox.hartshorn.core.context;

import org.dockbox.hartshorn.core.CustomMultiMap;
import org.dockbox.hartshorn.core.HashSetMultiMap;
import org.dockbox.hartshorn.core.Key;
import org.dockbox.hartshorn.core.MultiMap;
import org.dockbox.hartshorn.core.StringUtilities;
import org.dockbox.hartshorn.core.annotations.context.AutoCreating;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.domain.Exceptional;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The default implementation of {@link Context}. This implementation uses a {@link HashSetMultiMap} to store the
 * contexts.
 */
public abstract class DefaultContext implements Context {

    protected final transient Set<Context> contexts = ConcurrentHashMap.newKeySet();
    protected final transient MultiMap<String, Context> namedContexts = new CustomMultiMap<>(ConcurrentHashMap::newKeySet);

    @Override
    public <C extends Context> void add(final C context) {
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
    public <C extends Context> Exceptional<C> first(final ApplicationContext applicationContext, final Class<C> context) {
        return Exceptional.of(this.contexts.stream()
                        .filter(c -> TypeContext.unproxy(applicationContext, c).childOf(context))
                        .findFirst())
                .orElse(() -> {
                    final TypeContext<C> typeContext = TypeContext.of(context);
                    if (typeContext.annotation(AutoCreating.class).present()) {
                        applicationContext.log().debug("Context with key " + Key.of(context) + " does not exist in current context (" + TypeContext.of(this).name() + "), but is marked to be automatically created");
                        final C created = applicationContext.get(context);
                        this.add(created);
                        return created;
                    }
                    else return null;
                })
                .map(c -> (C) c);
    }

    @Override
    public <C extends Context> Exceptional<C> first(final ApplicationContext applicationContext, final Class<C> context, final String name) {
        return Exceptional.of(this.namedContexts.get(name).stream()
                        .filter(c -> TypeContext.of(c).childOf(context))
                        .findFirst())
                .orElse(() -> {
                    final TypeContext<C> typeContext = TypeContext.of(context);
                    if (typeContext.annotation(AutoCreating.class).present()) {
                        applicationContext.log().debug("Context with key " + Key.of(context, name) + " does not exist in current context (" + TypeContext.of(this).name() + "), but is marked to be automatically created");
                        final C created = applicationContext.get(context);
                        this.add(name, created);
                        return created;
                    }
                    else return null;
                })
                .map(c -> (C) c);
    }

    @Override
    public <C extends Context> Exceptional<C> first(final ApplicationContext applicationContext, final Key<C> key) {
        if (key.name() == null) return this.first(applicationContext, key.type().type());
        else return this.first(applicationContext, key.type().type(), key.name().value());
    }

    @Override
    public Exceptional<Context> first(final String name) {
        return Exceptional.of(this.namedContexts.get(name).stream().findFirst());
    }

    @Override
    public <N extends Context> Exceptional<N> first(final String name, final Class<N> context) {
        return Exceptional.of(this.namedContexts.get(name).stream()
                        .filter(c -> TypeContext.of(c).childOf(context))
                        .findFirst())
                .map(c -> (N) c);
    }

    @Override
    public <C extends Context> List<C> all(final Class<C> context) {
        return this.contexts.stream()
                .filter(c -> c.getClass().equals(context))
                .map(c -> (C) c)
                .toList();
    }

    @Override
    public List<Context> all(final String name) {
        return List.copyOf(this.namedContexts.get(name));
    }

    @Override
    public <N extends Context> List<N> all(final String name, final Class<N> context) {
        return this.namedContexts.get(name).stream()
                .filter(c -> TypeContext.of(c).childOf(context))
                .map(c -> (N) c)
                .toList();
    }
}
