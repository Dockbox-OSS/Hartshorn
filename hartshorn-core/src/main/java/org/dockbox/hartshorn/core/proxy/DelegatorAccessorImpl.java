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

package org.dockbox.hartshorn.core.proxy;

import org.dockbox.hartshorn.core.annotations.inject.Binds;
import org.dockbox.hartshorn.core.annotations.inject.Bound;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.domain.Exceptional;

import javax.inject.Inject;

import lombok.RequiredArgsConstructor;

@Binds(DelegatorAccessor.class)
@RequiredArgsConstructor(onConstructor_ = @Bound)
public class DelegatorAccessorImpl<T> implements DelegatorAccessor<T> {

    @Inject
    private ApplicationContext context;
    private final ProxyHandler<T> handler;

    @Override
    public <A> Exceptional<A> delegator(final Class<A> type) {
        if (!this.handler.type().childOf(type)) return Exceptional.empty();
        final TypeContext<A> typeContext = TypeContext.of(type);
        return this.context.environment().application().delegator(typeContext, (ProxyHandler<A>) this.handler);
    }
}
