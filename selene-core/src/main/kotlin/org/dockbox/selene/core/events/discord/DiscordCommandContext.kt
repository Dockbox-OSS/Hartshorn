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

package org.dockbox.selene.core.events.discord

import java.time.LocalDateTime
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.entities.User
import org.dockbox.selene.core.DiscordUtils
import org.dockbox.selene.core.i18n.common.ResourceEntry
import org.dockbox.selene.core.server.Selene
import org.dockbox.selene.core.text.Text

class DiscordCommandContext(val author: User, val channel: MessageChannel, val timeReceived: LocalDateTime, val command: String, val arguments: Array<String>) {

    fun sendToChannel(text: Text) {
        Selene.getInstance(DiscordUtils::class.java).sendToTextChannel(text, this.channel)
    }

    fun sendToChannel(text: CharSequence) {
        Selene.getInstance(DiscordUtils::class.java).sendToTextChannel(text, this.channel)
    }

    fun sendToChannel(text: ResourceEntry) {
        Selene.getInstance(DiscordUtils::class.java).sendToTextChannel(text, this.channel)
    }

    fun sendToAuthor(text: Text) {
        Selene.getInstance(DiscordUtils::class.java).sendToUser(text, this.author)
    }

    fun sendToAuthor(text: CharSequence) {
        Selene.getInstance(DiscordUtils::class.java).sendToUser(text, this.author)
    }

    fun sendToAuthor(text: ResourceEntry) {
        Selene.getInstance(DiscordUtils::class.java).sendToUser(text, this.author)
    }

}
