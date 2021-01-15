/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.dave;

import org.dockbox.selene.core.annotations.i18n.Resources;
import org.dockbox.selene.core.i18n.entry.Resource;

@Resources(responsibleExtension = DaveExtension.class)
public final class DaveResources {

    public static final Resource DAVE_LINK_SUGGESTION = new Resource("Here's a useful link, $1{0}", "dave.suggestion.link");
    public static final Resource DAVE_LINK_SUGGESTION_HOVER = new Resource("$2Click to open $1{0}", "dave.suggestion.link.hover");
    public static final Resource DAVE_DISCORD_FORMAT = new Resource("**Dave** ≫ {0}", "dave.format.discord");
    public static final Resource DAVE_MUTED = new Resource("$4Muted Dave, note that important triggers will always show", "dave.mute");
    public static final Resource DAVE_UNMUTED = new Resource("$1Unmuted Dave", "dave.unmute");
    public static final Resource DAVE_RELOADED_USER = new Resource("$1Reloaded Dave without breaking stuff, whoo!", "dave.reload");
    public static final Resource DAVE_TRIGGER_LIST_ITEM = new Resource("$3 - $1{0}", "dave.trigger.single");
    public static final Resource DAVE_TRIGGER_HOVER = new Resource("$1Click to perform trigger", "dave.trigger.single.hover");
    public static final Resource DAVE_TRIGGER_HEADER = new Resource("$1Triggers", "dave.trigger.header");
    public static final Resource NO_MATCHING_TRIGGER = new Resource("$4No trigger with id '{0}' exists.", "dave.trigger.notfound");

    private DaveResources() {}
}
