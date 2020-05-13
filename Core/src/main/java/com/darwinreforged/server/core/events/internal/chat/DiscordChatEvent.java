package com.darwinreforged.server.core.events.internal.chat;

import com.darwinreforged.server.core.events.util.Event;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

/**
 The type Discord chat event.
 */
public class DiscordChatEvent extends Event {

    private final Member member;
    private final Message message;
    private final Guild guild;
    private final TextChannel channel;

    /**
     Instantiates a new Discord chat event.

     @param member
     the member
     @param message
     the message
     @param guild
     the guild
     @param channel
     the channel
     */
    public DiscordChatEvent(Member member, Message message, Guild guild, TextChannel channel) {
        this.member = member;
        this.message = message;
        this.guild = guild;
        this.channel = channel;
    }

    /**
     Gets member.

     @return the member
     */
    public Member getMember() {
        return member;
    }

    /**
     Gets message.

     @return the message
     */
    public Message getMessage() {
        return message;
    }

    /**
     Gets guild.

     @return the guild
     */
    public Guild getGuild() {
        return guild;
    }

    /**
     Gets channel.

     @return the channel
     */
    public TextChannel getChannel() {
        return channel;
    }
}
