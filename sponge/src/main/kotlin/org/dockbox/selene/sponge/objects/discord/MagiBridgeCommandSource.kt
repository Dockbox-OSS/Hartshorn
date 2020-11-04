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

package org.dockbox.selene.sponge.objects.discord

import com.magitechserver.magibridge.util.BridgeCommandSource
import org.dockbox.selene.core.i18n.common.ResourceEntry
import org.dockbox.selene.core.objects.discord.DiscordCommandSource
import org.dockbox.selene.core.text.Text
import org.dockbox.selene.core.text.navigation.Pagination
import org.dockbox.selene.sponge.util.SpongeConversionUtil
import org.spongepowered.api.Sponge

class MagiBridgeCommandSource(private val bridge: BridgeCommandSource) : DiscordCommandSource() {

    override fun send(text: ResourceEntry) {
        bridge.sendMessage(SpongeConversionUtil.toSponge(Text.of(text)))
    }

    override fun send(text: Text) {
        bridge.sendMessage(SpongeConversionUtil.toSponge(text))
    }

    override fun send(text: CharSequence) {
        bridge.sendMessage(SpongeConversionUtil.toSponge(Text.of(text)))
    }

    override fun sendWithPrefix(text: ResourceEntry) {
        bridge.sendMessage(SpongeConversionUtil.toSponge(Text.of(text)))
    }

    override fun sendWithPrefix(text: Text) {
        bridge.sendMessage(SpongeConversionUtil.toSponge(text))
    }

    override fun sendWithPrefix(text: CharSequence) {
        bridge.sendMessage(SpongeConversionUtil.toSponge(Text.of(text)))
    }

    override fun sendPagination(pagination: Pagination) {
        return // TODO
    }

    override fun execute(command: String) {
        Sponge.getCommandManager().process(bridge, command)
    }
}
