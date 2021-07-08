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

package org.dockbox.hartshorn.sponge.event;

import org.dockbox.hartshorn.api.events.annotations.Posting;
import org.dockbox.hartshorn.server.minecraft.events.moderation.IpBannedEvent;
import org.dockbox.hartshorn.server.minecraft.events.moderation.IpUnbannedEvent;
import org.dockbox.hartshorn.server.minecraft.events.moderation.KickEvent;
import org.dockbox.hartshorn.server.minecraft.events.moderation.NameBannedEvent;
import org.dockbox.hartshorn.server.minecraft.events.moderation.NameUnbannedEvent;
import org.dockbox.hartshorn.server.minecraft.events.moderation.PlayerBannedEvent;
import org.dockbox.hartshorn.server.minecraft.events.moderation.PlayerNotedEvent;
import org.dockbox.hartshorn.server.minecraft.events.moderation.PlayerUnbannedEvent;
import org.dockbox.hartshorn.server.minecraft.events.moderation.PlayerWarnedEvent;
import org.dockbox.hartshorn.server.minecraft.events.moderation.PlayerWarningExpired;

@Posting(value = {
        // Ban events
        PlayerBannedEvent.class,
        IpBannedEvent.class,
        NameBannedEvent.class,
        // Unban events
        PlayerUnbannedEvent.class,
        IpUnbannedEvent.class,
        NameUnbannedEvent.class,
        // Remaining events
        KickEvent.class,
        PlayerWarnedEvent.class,
        PlayerWarningExpired.class,
        PlayerNotedEvent.class
})
public class ModerationEventBridge implements EventBridge {
}
