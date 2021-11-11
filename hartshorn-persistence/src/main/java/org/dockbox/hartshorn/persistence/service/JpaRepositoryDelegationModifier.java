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

package org.dockbox.hartshorn.persistence.service;

import org.dockbox.hartshorn.core.annotations.service.AutomaticActivation;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.proxy.ProxyHandler;
import org.dockbox.hartshorn.core.services.ProxyDelegationModifier;
import org.dockbox.hartshorn.persistence.jpa.JpaRepository;
import org.dockbox.hartshorn.persistence.annotations.UsePersistence;

@AutomaticActivation
public class JpaRepositoryDelegationModifier extends ProxyDelegationModifier<JpaRepository, UsePersistence> {
    @Override
    public Class<UsePersistence> activator() {
        return UsePersistence.class;
    }

    @Override
    protected Class<JpaRepository> parentTarget() {
        return JpaRepository.class;
    }

    @Override
    protected JpaRepository concreteDelegator(final ApplicationContext context, final ProxyHandler<JpaRepository> handler, final TypeContext<? extends JpaRepository> parent) {
        final Class<?> type = parent.typeParameters(JpaRepository.class).get(0).type();
        return context.get(JpaRepositoryFactory.class).repository(type);
    }
}
