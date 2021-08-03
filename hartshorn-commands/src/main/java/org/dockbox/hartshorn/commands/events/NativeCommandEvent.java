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

package org.dockbox.hartshorn.commands.events;

import org.dockbox.hartshorn.commands.CommandSource;
import org.dockbox.hartshorn.events.AbstractTargetCancellableEvent;

import lombok.Getter;
import lombok.Setter;

/**
 * The event fired when a command is executed natively through the implemented platform. This
 * typically includes both external commands and commands defined within Hartshorn.
 */
@Getter
@Setter
public class NativeCommandEvent extends AbstractTargetCancellableEvent {

    private String alias;
    private String[] arguments;

    public NativeCommandEvent(CommandSource source, String alias, String[] arguments) {
        super(source);
        this.alias = alias;
        this.arguments = arguments;
    }
}
