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

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.events.annotations.Posting;
import org.dockbox.hartshorn.i18n.text.Text;
import org.dockbox.hartshorn.server.minecraft.events.chat.SendChatEvent;
import org.dockbox.hartshorn.server.minecraft.players.Player;
import org.dockbox.hartshorn.sponge.util.SpongeAdapter;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.message.PlayerChatEvent;

@Posting(SendChatEvent.class)
public class ChatEventBridge extends EventBridge {

    @Listener
    public void on(final PlayerChatEvent event) {
        final Exceptional<Player> player = this.player(event);
        if (player.absent()) return;

        final Text text = SpongeAdapter.fromSponge(event.originalMessage());
        this.post(new SendChatEvent(player.get(), text), event);
    }

}
