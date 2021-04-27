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

import org.dockbox.selene.api.i18n.annotations.Resource;
import org.dockbox.selene.api.i18n.annotations.Resources;
import org.dockbox.selene.api.i18n.common.ResourceEntry;

@Resources(WorldManagement.class)
public interface WorldManagementResources {

    @Resource(value = "$2{0} $1was added to the blacklist and will not be unloaded", key = "worldmanagement.unloader.blacklist.added")
    ResourceEntry getBlacklistAdded(String world);

    @Resource(value = "$2{0} $1could not be blacklisted, are you sure it exists?", key = "worldmanagement.unloader.blacklist.failed")
    ResourceEntry getBlacklistFailure(String world);

}
