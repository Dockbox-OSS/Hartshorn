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

package org.dockbox.darwin.sponge.util.discord

import com.magitechserver.magibridge.MagiBridge
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Category
import net.dv8tion.jda.api.entities.TextChannel
import org.dockbox.darwin.core.i18n.common.ResourceEntry
import org.dockbox.darwin.core.text.Text
import org.dockbox.darwin.core.util.discord.DiscordUtils
import java.util.*

class SpongeDiscordUtils : DiscordUtils {

    override fun getJDA(): Optional<JDA> = Optional.ofNullable(MagiBridge.getInstance().jda)

    override fun getGlobalTextChannel(): Optional<TextChannel> {
        val cc = MagiBridge.getInstance().config.CHANNELS
        if (cc is TextChannel) return Optional.of(cc)
        return Optional.empty()
    }

    override fun getLoggingCategory(): Optional<Category> {
        if (getJDA().isPresent) return Optional.ofNullable(getJDA().get().getCategoryById("638683800167251998"))
        return Optional.empty()
    }

    override fun sendToTextChannel(text: Text, channel: TextChannel) {
        sendToTextChannel(text.toPlain(), channel)
    }

    override fun sendToTextChannel(text: CharSequence, channel: TextChannel) {
        channel.sendMessage(text).queue()
    }

    override fun sendToTextChannel(text: ResourceEntry, channel: TextChannel) {
        sendToTextChannel(text.plain(), channel)
    }


}
