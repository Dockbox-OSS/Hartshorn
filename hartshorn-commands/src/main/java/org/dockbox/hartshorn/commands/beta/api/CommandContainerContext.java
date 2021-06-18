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

package org.dockbox.hartshorn.commands.beta.api;

import org.dockbox.hartshorn.api.i18n.permissions.Permission;
import org.dockbox.hartshorn.commands.values.AbstractArgumentElement;
import org.dockbox.hartshorn.di.context.Context;

import java.time.temporal.ChronoUnit;
import java.util.List;

public interface CommandContainerContext extends Context {

    List<String> aliases();
    String arguments();
    Permission permission();
    long cooldown();
    ChronoUnit cooldownUnit();
    boolean inherited();
    boolean extended();
    boolean confirmation();
    Class<?> parent();
    List<AbstractArgumentElement<?>> elements();
    boolean matches(String command);
}