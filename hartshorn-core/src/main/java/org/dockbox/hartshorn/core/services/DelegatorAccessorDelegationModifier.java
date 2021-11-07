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

package org.dockbox.hartshorn.core.services;

import org.dockbox.hartshorn.core.annotations.proxy.UseProxying;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.properties.Attribute;
import org.dockbox.hartshorn.core.proxy.DelegatorAccessor;
import org.dockbox.hartshorn.core.proxy.ProxyHandler;

public class DelegatorAccessorDelegationModifier extends ProxyDelegationModifier<DelegatorAccessor, UseProxying> {

    @Override
    public Class<UseProxying> activator() {
        return UseProxying.class;
    }

    @Override
    protected Class<DelegatorAccessor> parentTarget() {
        return DelegatorAccessor.class;
    }

    @Override
    protected DelegatorAccessor concreteDelegator(final ApplicationContext context, final ProxyHandler<DelegatorAccessor> handler, final TypeContext<? extends DelegatorAccessor> parent, final Attribute<?>... attributes) {
        return context.get(AccessorFactory.class).delegatorAccessor(handler);
    }
}
