package org.dockbox.darwin.sponge.util.discord

import com.magitechserver.magibridge.MagiBridge
import net.dv8tion.jda.api.JDA
import org.dockbox.darwin.core.util.discord.DiscordUtils
import java.util.*

class SpongeDiscordUtils : DiscordUtils {

    override fun getJDA(): Optional<JDA> = Optional.ofNullable(MagiBridge.getInstance().jda)
}
