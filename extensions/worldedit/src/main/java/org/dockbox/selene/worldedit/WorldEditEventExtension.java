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

package org.dockbox.selene.worldedit;

import org.dockbox.selene.core.annotations.event.Listener;
import org.dockbox.selene.core.annotations.event.filter.Filter;
import org.dockbox.selene.core.annotations.event.processing.Getter;
import org.dockbox.selene.core.annotations.extension.Extension;
import org.dockbox.selene.core.events.chat.NativeCommandEvent;
import org.dockbox.selene.core.events.parents.Cancellable;
import org.dockbox.selene.core.objects.player.Player;
import org.dockbox.selene.worldedit.worldedit.WorldEditCopyEvent;
import org.dockbox.selene.worldedit.worldedit.WorldEditPasteEvent;

@Extension(id = "worldedit-events", name = "WorldEdit Events", description = "Provides additional WorldEdit events for Selene",
           authors = "GuusLieben", dependencies = {"com.boydti.fawe", "com.sk89q.worldedit"})
public class WorldEditEventExtension {

    @Listener
    @Filter(param = "alias", value = "/copy")
    public void onWorldEditCopy(NativeCommandEvent nce,
                                @Getter("getSource") Player player
    ) {
        Cancellable event = new WorldEditCopyEvent(player).post();
        nce.setCancelled(event.isCancelled());
    }

    @Listener
    @Filter(param = "alias", value = "/paste")
    public void onWorldEditPaste(NativeCommandEvent nce,
                                @Getter("getSource") Player player
    ) {
        Cancellable event = new WorldEditPasteEvent(player).post();
        nce.setCancelled(event.isCancelled());
    }

}
