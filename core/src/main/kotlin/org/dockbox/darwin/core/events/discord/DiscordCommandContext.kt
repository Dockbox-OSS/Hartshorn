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

package org.dockbox.darwin.core.events.discord

import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.entities.User
import org.dockbox.darwin.core.i18n.common.ResourceEntry
import org.dockbox.darwin.core.server.ServerReference
import org.dockbox.darwin.core.text.Text
import org.dockbox.darwin.core.util.discord.DiscordUtils
import java.time.LocalDateTime

class DiscordCommandContext(val author: User, val channel: MessageChannel, val timeReceived: LocalDateTime, val command: String, val arguments: Array<String>) : ServerReference() {

    fun sendToChannel(text: Text) {
        getInstance(DiscordUtils::class.java).sendToTextChannel(text, this.channel)
    }

    fun sendToChannel(text: CharSequence) {
        getInstance(DiscordUtils::class.java).sendToTextChannel(text, this.channel)
    }

    fun sendToChannel(text: ResourceEntry) {
        getInstance(DiscordUtils::class.java).sendToTextChannel(text, this.channel)
    }

    fun sendToAuthor(text: Text) {
        getInstance(DiscordUtils::class.java).sendToUser(text, this.author)
    }

    fun sendToAuthor(text: CharSequence) {
        getInstance(DiscordUtils::class.java).sendToUser(text, this.author)
    }

    fun sendToAuthor(text: ResourceEntry) {
        getInstance(DiscordUtils::class.java).sendToUser(text, this.author)
    }

}
