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

package org.dockbox.selene.discord;

import org.dockbox.selene.api.i18n.annotations.Resources;
import org.dockbox.selene.api.i18n.common.ResourceEntry;
import org.dockbox.selene.api.i18n.entry.Resource;

@Resources(DiscordUtils.class)
public class DiscordResources {

    public static final ResourceEntry DISCORD_COMMAND_UNKNOWN =
            new Resource("Sorry, I don't know what to do with that command!", "discord.command.unknown");
    public static final ResourceEntry DISCORD_COMMAND_NOT_PERMITTED =
            new Resource("You are not permitted to use that command!", "discord.command.notpermitted");
    public static final ResourceEntry DISCORD_COMMAND_ERRORED =
            new Resource("Sorry, I could not start that command. Please report this in our support channel.", "discord.command.caught");

}
