package com.darwinreforged.server.core.util;

import com.darwinreforged.server.core.init.AbstractUtility;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

@AbstractUtility("Discord connection and messaging utilities")
public abstract class DiscordUtils {

    public void sendToChannel(String message, String channel) {
        getChannel(channel).sendMessage(message).queue();
    }

    public void sendToChannel(String message, long channel) {
        getChannel(channel).sendMessage(message).queue();
    }

    public void sendToChannel(MessageEmbed embed, String channel) {
        getChannel(channel).sendMessage(embed).queue();
    }

    public void sendToChannel(MessageEmbed embed, long channel) {
        getChannel(channel).sendMessage(embed).queue();
    }

    public TextChannel getChannel(String channel) {
        return getJda().getTextChannelById(channel);
    }

    public TextChannel getChannel(long channel) {
        return getJda().getTextChannelById(channel);
    }

    protected abstract JDA getJda();
}
