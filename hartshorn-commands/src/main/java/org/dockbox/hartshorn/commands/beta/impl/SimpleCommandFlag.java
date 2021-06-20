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

package org.dockbox.hartshorn.commands.beta.impl;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.i18n.permissions.Permission;
import org.dockbox.hartshorn.commands.beta.api.CommandFlag;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SimpleCommandFlag implements CommandFlag {

    private final String name;
    private final Permission permission;

    public SimpleCommandFlag(String name) {
        this.name = name;
        this.permission = null;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public Exceptional<Permission> permission() {
        return Exceptional.of(this.permission);
    }
}
