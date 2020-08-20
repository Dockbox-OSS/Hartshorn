package org.dockbox.darwin.core.util.discord

import net.dv8tion.jda.api.JDA
import java.util.*

interface DiscordUtils {

    fun getJDA(): Optional<JDA>

}
