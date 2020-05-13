package com.darwinreforged.server.core.util;

import com.darwinreforged.server.core.init.AbstractUtility;
import com.darwinreforged.server.core.init.DarwinConfig;
import com.darwinreforged.server.core.init.DarwinServer;
import com.darwinreforged.server.core.listeners.DiscordListener;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.List;

import javax.security.auth.login.LoginException;

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
        return getJdaWithFallback().getTextChannelById(channel);
    }

    public TextChannel getChannel(long channel) {
        return getJdaWithFallback().getTextChannelById(channel);
    }
    
    public User getUserById(long id) {
        return getJdaWithFallback().getUserById(id);
    }
    
    public User getUserById(String id) {
        return getJda().getUserById(id);
    }

    protected JDA getJdaWithFallback() {
        JDA jda = getJda();
        if (jda != null) return jda;
        else {
            try {
                DarwinServer.getLog().warn("Failed to obtain internal JDA instance, creating temporary instance..");
                return (new JDABuilder(DarwinConfig.BOT_TOKEN.get())).build().awaitReady();
            } catch (InterruptedException | LoginException e) {
                DarwinServer.error("Failed to obtain JDA instance", e);
            }
        }
        // We tried everything we could..
        DarwinServer.getLog().warn("Failed to obtain JDA instance");
        return null;
    }

    protected abstract JDA getJda();
}
