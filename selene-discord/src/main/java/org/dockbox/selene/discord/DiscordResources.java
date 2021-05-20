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

import org.dockbox.selene.api.i18n.annotations.Resource;
import org.dockbox.selene.api.i18n.common.ResourceEntry;
import org.dockbox.selene.di.annotations.Service;

@Service(owner = DiscordUtils.class)
public interface DiscordResources {

    @Resource("Sorry, I don't know what to do with that command!")
    ResourceEntry getCommandUnknown();

    @Resource("You are not permitted to use that command!")
    ResourceEntry getCommandNotPermitted();

    @Resource("Sorry, I could not start that command. Please report this in our support channel.")
    ResourceEntry getCommandCaught();

}
