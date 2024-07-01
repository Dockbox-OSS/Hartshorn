/*
 * Copyright 2019-2024 the original author or authors.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import org.dockbox.hartshorn.util.StringUtilities;
import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.collections.SynchronizedHashSetMultiMap;
import org.dockbox.hartshorn.util.option.Option;

/**
 * The default implementation of {@link Context}. This implementation uses a {@link SynchronizedHashSetMultiMap} to store the
 * contexts.
 *
 * @since 0.4.1
 *
 * @author Guus Lieben
 */
public abstract class DefaultContext implements Context {

    private transient Set<ContextView> unnamedContexts;
    private transient MultiMap<String, ContextView> namedContexts;

    /**
     * Returns all contexts that are not named.
     *
     * @return All contexts that are not named.
     */
    protected Set<ContextView> unnamedContexts() {
        if (this.unnamedContexts == null) {
            this.unnamedContexts = ConcurrentHashMap.newKeySet();
        }
        return this.unnamedContexts;
    }

    /**
     * Returns all contexts that are named. This does not guarantee that the contexts are unique, or that
     * they are an instance of {@link NamedContext}.
     *
     * @return All contexts that are named.
     */
    protected MultiMap<String, ContextView> namedContexts() {
        if (this.namedContexts == null) {
            this.namedContexts = new SynchronizedHashSetMultiMap<>();
        }
        return this.namedContexts;
    }

    @Override
    public <C extends ContextView> void addContext(C context) {
        if (context instanceof NamedContext named && StringUtilities.notEmpty(named.name())) {
            this.namedContexts().put(named.name(), context);
        }
        else if (context != null) {
            this.unnamedContexts().add(context);
        }
    }

    @Override
    public <C extends ContextView> void addContext(String name, C context) {
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
    public List<ContextView> contexts() {
        List<ContextView> contexts = new ArrayList<>();
        if (this.unnamedContexts != null) {
            contexts.addAll(this.unnamedContexts);
        }
        if (this.namedContexts != null) {
            contexts.addAll(this.namedContexts.allValues());
        }
        return Collections.unmodifiableList(contexts);
    }

    @Override
    public <C extends ContextView> Option<C> firstContext(ContextIdentity<C> key) {
        return Option.of(this.stream(key).findFirst())
                .orCompute(() -> {
                    C context = key.create();
                    this.addContext(context);
                    return context;
                });
    }

    @Override
    public <C extends ContextView> List<C> contexts(ContextIdentity<C> key) {
        return this.stream(key).toList();
    }

    protected <C extends ContextView> Stream<C> stream(ContextIdentity<C> key) {
        Stream<ContextView> contexts = StringUtilities.empty(key.name())
                ? this.unnamedContexts().stream()
                : this.namedContexts().get(key.name()).stream();

        return contexts.filter(key.type()::isInstance)
                .map(key.type()::cast);
    }

    @Override
    public <C extends ContextView> Option<C> firstContext(Class<C> context) {
        return this.firstContext(new SimpleContextIdentity<>(context));
    }

    @Override
    public <C extends ContextView> List<C> contexts(Class<C> context) {
        return this.contexts(new SimpleContextIdentity<>(context));
    }

    @Override
    public void copyToContext(Context context) {
        this.unnamedContexts().forEach(context::addContext);
        this.namedContexts().forEach(context::addContext);
    }

    @Override
    public ContextView contextView() {
        return this;
    }
}
