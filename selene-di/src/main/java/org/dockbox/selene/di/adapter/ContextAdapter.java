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

package org.dockbox.selene.di.adapter;

import org.dockbox.selene.di.inject.Injector;
import org.dockbox.selene.di.services.ServiceLocator;

import lombok.Getter;

@Getter
public final class ContextAdapter {

    private final Injector injector;
    private final ServiceLocator locator;

    public ContextAdapter(InjectSource inject, ServiceSource service) {
        this.injector = inject.create();
        this.locator = service.create();
    }

}
