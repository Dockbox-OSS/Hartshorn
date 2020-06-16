package org.dockbox.darwin.core.events.discord

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel
import org.dockbox.darwin.core.objects.events.Event

abstract class DiscordEvent : Event {

    class Chat(
            val member: Member,
            val message: Message,
            val guild: Guild,
            val channel: TextChannel
    )

}
