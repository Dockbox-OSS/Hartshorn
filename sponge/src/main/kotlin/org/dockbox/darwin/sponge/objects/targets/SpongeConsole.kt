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

package org.dockbox.darwin.sponge.objects.targets

import com.google.inject.Singleton
import org.dockbox.darwin.core.i18n.I18N
import org.dockbox.darwin.core.objects.targets.Console
import org.dockbox.darwin.core.server.Server
import org.dockbox.darwin.core.text.Text
import org.dockbox.darwin.core.text.Text.Companion.of
import org.dockbox.darwin.sponge.util.SpongeConversionUtil
import org.spongepowered.api.Sponge

@Singleton
class SpongeConsole private constructor() : Console() {
    override fun execute(command: String) {
        Sponge.getCommandManager().process(
                Sponge.getServer().console, command)
    }

    override fun send(text: I18N) {
        send(of(text.getValue(Server.getServer().getGlobalConfig().getDefaultLanguage())))
    }

    override fun send(text: Text) {
        Sponge.getServer().console.sendMessage(SpongeConversionUtil.toSponge(text))
    }

    override fun send(text: CharSequence) {
        send(of(text))
    }

    override fun sendWithPrefix(text: I18N) {
        sendWithPrefix(of(text.getValue(Server.getServer().getGlobalConfig().getDefaultLanguage())))
    }

    override fun sendWithPrefix(text: Text) {
        Sponge.getServer().console.sendMessage(org.spongepowered.api.text.Text.of(
                SpongeConversionUtil.toSponge(I18N.PREFIX.asText()),
                SpongeConversionUtil.toSponge(text)
        ))
    }

    override fun sendWithPrefix(text: CharSequence) {
        sendWithPrefix(of(text))
    }

    companion object {
        val instance = SpongeConsole()
    }
}
