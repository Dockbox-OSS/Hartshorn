package com.darwinreforged.server.core.events.internal.chat;

import com.darwinreforged.server.core.events.util.Event;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Invite.Channel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

public class DiscordChatEvent extends Event {

    private final Member member;
    private final Message message;
    private final Guild guild;
    private final Channel channel;

    public DiscordChatEvent(Member member, Message message, Guild guild, Channel channel) {
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

    public Channel getChannel() {
        return channel;
    }
}
