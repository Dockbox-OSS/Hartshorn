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

import org.dockbox.hartshorn.api.i18n.annotations.Resource;
import org.dockbox.hartshorn.api.i18n.common.ResourceEntry;
import org.dockbox.hartshorn.di.annotations.Service;

@Service(owner = Dave.class)
public interface DaveResources {

    @Resource("Here's a useful link, $1{0}")
    ResourceEntry getSuggestionLink(String link);

    @Resource("$2Click to open $1{0}")
    ResourceEntry getSuggestionLinkHover(String link);

    @Resource("**Dave** ≫ {0}")
    ResourceEntry getDiscordFormat(String message);

    @Resource("$4Muted Dave, note that important triggers will always show")
    ResourceEntry getMute();

    @Resource("$1Unmuted Dave")
    ResourceEntry getUnmute();

    @Resource("$1Reloaded Dave without breaking stuff, whoo!")
    ResourceEntry getReload();

    @Resource("$3 - $1{0}")
    ResourceEntry getTriggerSingle(String item);

    @Resource("$1Click to perform trigger")
    ResourceEntry getTriggerSingleHover();

    @Resource("$1Triggers")
    ResourceEntry getTriggerHeader();

    @Resource("$4No trigger with id '{0}' exists.")
    ResourceEntry getTriggerNotfound(String trigger);
}
