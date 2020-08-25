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
