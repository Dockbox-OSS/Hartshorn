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

package org.dockbox.darwin.core.util.discord

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.*
import org.dockbox.darwin.core.events.discord.DiscordCommandContext
import org.dockbox.darwin.core.i18n.common.ResourceEntry
import org.dockbox.darwin.core.text.Text
import java.util.*

interface DiscordUtils {

    fun getJDA(): Optional<JDA>

    fun getGlobalTextChannel(): Optional<TextChannel>
    fun getLoggingCategory(): Optional<Category>
    fun getGuild(): Optional<Guild>

    fun sendToTextChannel(text: Text, channel: MessageChannel)
    fun sendToTextChannel(text: CharSequence, channel: MessageChannel)

    fun sendToTextChannel(text: ResourceEntry, channel: MessageChannel)
    fun sendToUser(text: Text, user: User)
    fun sendToUser(text: CharSequence, user: User)

    fun sendToUser(text: ResourceEntry, user: User)
    fun registerCommandListener(instance: Any)
    fun post(command: String, context: DiscordCommandContext)
}
