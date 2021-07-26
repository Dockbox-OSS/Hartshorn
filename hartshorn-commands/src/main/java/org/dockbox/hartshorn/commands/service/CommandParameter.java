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

package org.dockbox.hartshorn.commands.service;

import org.dockbox.hartshorn.commands.context.CommandContext;
import org.dockbox.hartshorn.util.HartshornUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents a single parameter which can be provided to command executors
 * through {@link CommandContext#flags()} and {@link CommandContext#arguments()}.
 * @param <T> The type of the parameter value.
 */
@AllArgsConstructor
@Getter
public class CommandParameter<T> {

    private final T value;
    private final String key;

    public String trimmedKey() {
        return HartshornUtils.trimWith('-', this.key());
    }

}
