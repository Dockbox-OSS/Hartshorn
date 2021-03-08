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

package org.dockbox.selene.api.command.context;

import org.dockbox.selene.api.command.context.CommandValue.Argument;
import org.dockbox.selene.api.command.context.CommandValue.Flag;
import org.dockbox.selene.api.command.source.CommandSource;
import org.dockbox.selene.api.objects.Exceptional;
import org.dockbox.selene.api.objects.location.Location;
import org.dockbox.selene.api.objects.location.World;

public interface CommandContext {

    String alias();

    int arguments();

    int flags();

    <T> Exceptional<Argument<T>> argument(String key);

    <T> T get(String key);

    <T> Exceptional<T> optional(String key);

    <T> Exceptional<Flag<T>> flag(String key);

    boolean has(String key);

    CommandSource sender();

    Exceptional<Location> location();

    Exceptional<World> world();

    String[] permissions();
}
