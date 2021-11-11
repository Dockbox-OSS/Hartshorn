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

import org.dockbox.hartshorn.core.annotations.service.Service;
import org.dockbox.hartshorn.core.boot.LifecycleObserver;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.persistence.annotations.UsePersistence;
import org.dockbox.hartshorn.persistence.context.EntityContext;

import java.util.Collection;

import javax.persistence.Entity;

@Service(activators = UsePersistence.class)
public class PersistentTypeService implements LifecycleObserver {

    @Override
    public void onCreated(ApplicationContext applicationContext) {
        // Nothing happens
    }

    @Override
    public void onStarted(ApplicationContext applicationContext) {
        final Collection<TypeContext<?>> entities = applicationContext.environment().types(Entity.class);
        applicationContext.add(new EntityContext(entities));
    }
}
