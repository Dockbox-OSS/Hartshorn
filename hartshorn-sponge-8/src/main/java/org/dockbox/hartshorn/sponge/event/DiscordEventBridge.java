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

import org.dockbox.hartshorn.discord.events.DiscordBotDisconnectedEvent;
import org.dockbox.hartshorn.discord.events.DiscordBotReconnectedEvent;
import org.dockbox.hartshorn.discord.events.DiscordChatDeletedEvent;
import org.dockbox.hartshorn.discord.events.DiscordChatReceivedEvent;
import org.dockbox.hartshorn.discord.events.DiscordChatUpdatedEvent;
import org.dockbox.hartshorn.discord.events.DiscordPrivateChatDeletedEvent;
import org.dockbox.hartshorn.discord.events.DiscordPrivateChatReceivedEvent;
import org.dockbox.hartshorn.discord.events.DiscordPrivateChatUpdatedEvent;
import org.dockbox.hartshorn.discord.events.DiscordReactionAddedEvent;
import org.dockbox.hartshorn.discord.events.DiscordUserBannedEvent;
import org.dockbox.hartshorn.discord.events.DiscordUserJoinedEvent;
import org.dockbox.hartshorn.discord.events.DiscordUserLeftEvent;
import org.dockbox.hartshorn.discord.events.DiscordUserNicknameChangedEvent;
import org.dockbox.hartshorn.discord.events.DiscordUserUnbannedEvent;
import org.dockbox.hartshorn.events.annotations.Posting;

@Posting({
        DiscordChatReceivedEvent.class,
        DiscordUserUnbannedEvent.class,
        DiscordUserNicknameChangedEvent.class,
        DiscordPrivateChatUpdatedEvent.class,
        DiscordUserBannedEvent.class,
        DiscordBotReconnectedEvent.class,
        DiscordUserJoinedEvent.class,
        DiscordReactionAddedEvent.class,
        DiscordUserLeftEvent.class,
        DiscordChatDeletedEvent.class,
        DiscordChatUpdatedEvent.class,
        DiscordPrivateChatDeletedEvent.class,
        DiscordBotDisconnectedEvent.class,
        DiscordPrivateChatReceivedEvent.class
})
public class DiscordEventBridge extends EventBridge {
}
