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

package org.dockbox.selene.worldmanagement;

import org.dockbox.selene.api.annotations.i18n.Resources;
import org.dockbox.selene.api.i18n.common.ResourceEntry;
import org.dockbox.selene.api.i18n.entry.Resource;

@Resources(module = WorldManagement.class)
public class WorldManagementResources {

    public static final ResourceEntry WORLD_BLACKLIST_ADDED = new Resource("$2{0} $1was added to the blacklist and will not be unloaded", "worldmanagement.unloader.blacklist.added");
    public static final ResourceEntry WORLD_BLACKLIST_FAILED = new Resource("$2{0} $1could not be blacklisted, are you sure it exists?", "worldmanagement.unloader.blacklist.failed");

}
