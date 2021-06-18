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

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.i18n.permissions.Permission;
import org.dockbox.hartshorn.commands.context.CommandParameter;
import org.dockbox.hartshorn.commands.source.CommandSource;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;

public interface ParsedContext extends ParserContext {

    <T> T get(String key);

    boolean has(String key);

    <T> Exceptional<T> first(String key);

    <T> Exceptional<CommandParameter<T>> argument(String key);

    <T> Exceptional<CommandParameter<T>> flag(String key);

    CommandSource getSender();

    @UnmodifiableView
    List<Permission> getPermissions();

    String command();
}