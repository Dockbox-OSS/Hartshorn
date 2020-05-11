package com.darwinreforged.server.core.util;

import com.darwinreforged.server.core.init.AbstractUtility;
import com.darwinreforged.server.core.listeners.DiscordListener;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.List;

@AbstractUtility("Discord connection and messaging utilities")
public abstract class DiscordUtils {

    public void init(List<String> channelWhitelist) {
        if (getJda() != null) getJda().addEventListener(new DiscordListener(channelWhitelist));
    }

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
    
    public User getUserById(long id) {
        return getJda().getUserById(id);
    }
    
    public User getUserById(String id) {
        return getJda().getUserById(id);
    }

    protected abstract JDA getJda();
}
