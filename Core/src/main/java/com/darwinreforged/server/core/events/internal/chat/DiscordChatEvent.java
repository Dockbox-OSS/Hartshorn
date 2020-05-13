package com.darwinreforged.server.core.events.internal.chat;

import com.darwinreforged.server.core.events.util.Event;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class DiscordChatEvent extends Event {

    private final Member member;
    private final Message message;
    private final Guild guild;
    private final TextChannel channel;

    public DiscordChatEvent(Member member, Message message, Guild guild, TextChannel channel) {
        this.member = member;
        this.message = message;
        this.guild = guild;
        this.channel = channel;
    }

    public Member getMember() {
        return member;
    }

    public Message getMessage() {
        return message;
    }

    public Guild getGuild() {
        return guild;
    }

    public TextChannel getChannel() {
        return channel;
    }
}
