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

package org.dockbox.hartshorn.di.context;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.di.ApplicationContextAware;
import org.dockbox.hartshorn.di.annotations.context.AutoCreating;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.dockbox.hartshorn.util.Reflect;

import java.util.List;
import java.util.Set;

public abstract class DefaultContext implements Context {

    protected final transient Set<Context> contexts = HartshornUtils.emptyConcurrentSet();
    protected final transient Multimap<String, Context> namedContexts = HashMultimap.create();

    @Override
    public <C extends Context> void add(C context) {
        if (context != null) this.contexts.add(context);
    }

    @Override
    public <N extends NamedContext> void add(N context) {
        if (context != null && HartshornUtils.notEmpty(context.name()))
            this.namedContexts.put(context.name(), context);
    }

    @Override
    public <C extends Context> void add(String name, C context) {
        if (context != null && HartshornUtils.notEmpty(name))
            this.namedContexts.put(name, context);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <C extends Context> Exceptional<C> first(Class<C> context) {
        return Exceptional.of(this.contexts.stream()
                .filter(c -> Reflect.assigns(context, c.getClass()))
                .findFirst())
                .orElse(() -> {
                    if (Reflect.has(context, AutoCreating.class)) {
                        final C created = ApplicationContextAware.instance().context().get(context);
                        this.add(created);
                        return created;
                    } else return null;
                })
                .map(c -> (C) c);
    }

    @Override
    public Exceptional<Context> first(String name) {
        return Exceptional.of(this.namedContexts.get(name).stream().findFirst());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <N extends Context> Exceptional<N> first(String name, Class<N> context) {
        return Exceptional.of(this.namedContexts.get(name).stream()
                .filter(c -> Reflect.assigns(context, c.getClass()))
                .findFirst())
                .map(c -> (N) c);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <C extends Context> List<C> all(Class<C> context) {
        return HartshornUtils.asUnmodifiableList(this.contexts.stream()
                .filter( c -> c.getClass().equals(context))
                .map(c -> (C) c)
                .toList());
    }

    @Override
    public List<Context> all(String name) {
        return HartshornUtils.asList(this.namedContexts.get(name));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <N extends Context> List<N> all(String name, Class<N> context) {
        return this.namedContexts.get(name).stream()
                .filter(c -> Reflect.assigns(context, c.getClass()))
                .map(c -> (N) c)
                .toList();
    }
}
