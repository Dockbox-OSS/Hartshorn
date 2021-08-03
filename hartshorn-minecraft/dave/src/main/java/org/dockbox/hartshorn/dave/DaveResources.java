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

package org.dockbox.hartshorn.dave;

import org.dockbox.hartshorn.i18n.annotations.Resource;
import org.dockbox.hartshorn.i18n.common.ResourceEntry;
import org.dockbox.hartshorn.di.annotations.service.Service;

@Service(owner = Dave.class)
public interface DaveResources {

    @Resource("Here's a useful link, $1{0}")
    ResourceEntry suggestionLink(String link);

    @Resource("$2Click to open $1{0}")
    ResourceEntry suggestionLinkHover(String link);

    @Resource("**Dave** ≫ {0}")
    ResourceEntry discordFormat(String message);

    @Resource("$4Muted Dave, note that important triggers will always show")
    ResourceEntry mute();

    @Resource("$1Unmuted Dave")
    ResourceEntry unmute();

    @Resource("$1Reloaded Dave without breaking stuff, whoo!")
    ResourceEntry reload();

    @Resource("$3 - $1{0}")
    ResourceEntry triggerSingle(String item);

    @Resource("$1Click to perform trigger")
    ResourceEntry triggerSingleHover();

    @Resource("$1Triggers")
    ResourceEntry triggerHeader();

    @Resource("$4No trigger with id '{0}' exists.")
    ResourceEntry triggerNotfound(String trigger);
}
