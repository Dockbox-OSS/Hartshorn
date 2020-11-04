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

package org.dockbox.selene.sponge.objects.targets

import com.google.inject.Singleton
import org.dockbox.selene.core.i18n.entry.IntegratedResource
import org.dockbox.selene.core.objects.targets.Console
import org.dockbox.selene.core.text.Text
import org.dockbox.selene.core.text.navigation.Pagination
import org.dockbox.selene.sponge.util.SpongeConversionUtil
import org.spongepowered.api.Sponge

/**
 * Sponge console implementation. Provides the instance only through SpongeConsole.instance
 */
@Singleton
class SpongeConsole private constructor() : Console() {
    override fun execute(command: String) {
        Sponge.getCommandManager().process(
                Sponge.getServer().console, command)
    }

    override fun send(text: Text) {
        Sponge.getServer().console.sendMessage(SpongeConversionUtil.toSponge(text))
    }

    override fun sendWithPrefix(text: Text) {
        Sponge.getServer().console.sendMessage(org.spongepowered.api.text.Text.of(
                SpongeConversionUtil.toSponge(IntegratedResource.PREFIX.asText()),
                SpongeConversionUtil.toSponge(text)
        ))
    }

    override fun sendPagination(pagination: Pagination) {
        SpongeConversionUtil.toSponge(pagination).sendTo(Sponge.getServer().console)
    }

    companion object {
        val instance = SpongeConsole()
    }
}
