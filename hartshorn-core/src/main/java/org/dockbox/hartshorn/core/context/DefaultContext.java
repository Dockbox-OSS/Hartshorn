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

package org.dockbox.hartshorn.core.context;

import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.HashSetMultiMap;
import org.dockbox.hartshorn.core.MultiMap;
import org.dockbox.hartshorn.core.annotations.context.AutoCreating;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.HartshornUtils;

import java.util.List;
import java.util.Set;

public abstract class DefaultContext implements Context {

    protected final transient Set<Context> contexts = HartshornUtils.emptyConcurrentSet();
    protected final transient MultiMap<String, Context> namedContexts = new HashSetMultiMap<>();

    @Override
    public <C extends Context> void add(final C context) {
        if (context != null) this.contexts.add(context);
    }

    @Override
    public <N extends NamedContext> void add(final N context) {
        if (context != null && HartshornUtils.notEmpty(context.name()))
            this.namedContexts.put(context.name(), context);
    }

    @Override
    public <C extends Context> void add(final String name, final C context) {
        if (context != null && HartshornUtils.notEmpty(name))
            this.namedContexts.put(name, context);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <C extends Context> Exceptional<C> first(final ApplicationContext applicationContext, final Class<C> context) {
        return Exceptional.of(this.contexts.stream()
                        .filter(c -> TypeContext.of(c).childOf(context))
                        .findFirst())
                .orElse(() -> {
                    final TypeContext<C> typeContext = TypeContext.of(context);
                    if (typeContext.annotation(AutoCreating.class).present()) {
                        applicationContext.log().debug("Context with type " + typeContext.name() + " does not exist in current context (" + TypeContext.of(this).name() + "), but is marked to be automatically created");
                        final C created = applicationContext.get(context);
                        this.add(created);
                        return created;
                    }
                    else return null;
                })
                .map(c -> (C) c);
    }

    @Override
    public Exceptional<Context> first(final String name) {
        return Exceptional.of(this.namedContexts.get(name).stream().findFirst());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <N extends Context> Exceptional<N> first(final String name, final Class<N> context) {
        return Exceptional.of(this.namedContexts.get(name).stream()
                        .filter(c -> TypeContext.of(c).childOf(context))
                        .findFirst())
                .map(c -> (N) c);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <C extends Context> List<C> all(final Class<C> context) {
        return HartshornUtils.asUnmodifiableList(this.contexts.stream()
                .filter(c -> c.getClass().equals(context))
                .map(c -> (C) c)
                .toList());
    }

    @Override
    public List<Context> all(final String name) {
        return HartshornUtils.asList(this.namedContexts.get(name));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <N extends Context> List<N> all(final String name, final Class<N> context) {
        return this.namedContexts.get(name).stream()
                .filter(c -> TypeContext.of(c).childOf(context))
                .map(c -> (N) c)
                .toList();
    }
}
